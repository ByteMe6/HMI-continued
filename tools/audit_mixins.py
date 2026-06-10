#!/usr/bin/env python3
"""Verify every @Mixin target class and every method-string target in the ported
source exists in the (unobfuscated) 26.1.2 client jar. Usage:
  python3 audit_mixins.py <mc-jar> <src-root> <mixins-json>"""
import json, re, subprocess, sys, zipfile, pathlib

mc_jar, src_root, mixins_json = sys.argv[1], pathlib.Path(sys.argv[2]), sys.argv[3]
active = json.load(open(mixins_json))
names = active.get("mixins", []) + active.get("client", [])
pkg = active["package"].replace(".", "/")
zf = zipfile.ZipFile(mc_jar)
zf_names = set(zf.namelist())
fail = 0

def javap(cls):
    return subprocess.run(["javap", "-p", "-classpath", mc_jar, cls],
                          capture_output=True, text=True).stdout

for m in names:
    path = src_root / (pkg + "/" + m.replace(".", "/") + ".java")
    src = path.read_text()
    # target classes: @Mixin(Foo.class) or @Mixin(targets = "a.b.C")
    classes = re.findall(r'@Mixin\(\s*\{?\s*(?:value\s*=\s*)?([A-Za-z0-9_.]+)\.class', src)
    classes += re.findall(r'@Mixin\(\s*targets\s*=\s*"([^"]+)"', src)
    # resolve simple names via imports: {SimpleName: fully.qualified.SimpleName}
    inv = {c: p + "." + c
           for p, c in re.findall(r'import\s+([a-z0-9_.]+)\.([A-Za-z0-9_$]+);', src)}
    for cls in classes:
        fq = cls if "." in cls else inv.get(cls, cls)
        entry = fq.replace(".", "/") + ".class"
        ok = entry in zf_names
        print(("OK   " if ok else "MISS ") + m + " -> " + fq)
        if not ok:
            fail += 1
        # method targets in this mixin vs javap of the target
        sigs = javap(fq) if ok else ""
        for meth in re.findall(r'method\s*=\s*\{?\s*"([^"(]+)', src):
            if meth in ("<init>", "<clinit>"):
                continue
            if re.search(r'\b' + re.escape(meth) + r'\s*\(', sigs):
                print("  OK   method " + meth)
            else:
                print("  MISS method " + meth + " (not found in " + fq + ")")
                fail += 1
        # @At INVOKE/FIELD targets referencing vanilla members
        for tgt in re.findall(r'target\s*=\s*"L([^";]+);([A-Za-z0-9_$<>]+)[(:]', src):
            tcls, tmember = tgt[0].replace("/", "."), tgt[1]
            if not tcls.startswith("net.minecraft") and not tcls.startswith("com.mojang"):
                continue
            tsigs = javap(tcls)
            if not tsigs:
                print("  MISS @At class " + tcls)
                fail += 1
                continue
            if tmember in ("<init>", "<clinit>"):
                continue
            if re.search(r'\b' + re.escape(tmember) + r'\b', tsigs):
                print("  OK   @At " + tcls + "." + tmember)
            else:
                print("  MISS @At " + tcls + "." + tmember)
                fail += 1
        # @Invoker/@Accessor named members
        for inv_name in re.findall(r'@(?:Invoker|Accessor)\("([A-Za-z0-9_$]+)"\)', src):
            if classes:
                tsigs = javap(inv.get(classes[0], classes[0]) if "." not in classes[0] else classes[0])
                if re.search(r'\b' + re.escape(inv_name) + r'\b', tsigs):
                    print("  OK   invoker/accessor " + inv_name)
                else:
                    print("  MISS invoker/accessor " + inv_name)
                    fail += 1
print("FAILURES:", fail)
sys.exit(1 if fail else 0)
