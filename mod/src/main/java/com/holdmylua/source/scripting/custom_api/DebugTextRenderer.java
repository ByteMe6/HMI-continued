package com.holdmylua.source.scripting.custom_api;

import com.holdmylua.source.annotation.Safe;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class DebugTextRenderer {
   private ArrayList<String> list = new ArrayList<>();

   public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list) {
      Set<T> set = new LinkedHashSet<>(list);
      list.clear();
      list.addAll(set);
      return list;
   }

   @Safe
   public void out(String line) {
      this.list.add(line);
   }

   public void clear() {
      this.list.clear();
   }

   public ArrayList<String> get() {
      return removeDuplicates(this.list);
   }
}
