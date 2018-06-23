package aps_project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Util {
    private static final Util instance = new Util();

    //private constructor to avoid client applications to use constructor
    private Util() {
    }

    public static Util getInstance() {
        return instance;
    }

    static <K> List<List<K>> getSequencesOfN(List<K> list, int n) {
        java.util.function.BiFunction<List<K>, List<List<K>>,
                List<List<K>>> go = (l, lofLists) -> {
            List<List<K>> res = new ArrayList<>();

            for (List<K> lObj : lofLists) {
                for (K obj : l) {
                    List<K> tmpLObj = new ArrayList<>(lObj);
                    tmpLObj.add(obj);
                    res.add(tmpLObj);
                }
            }

            return res;
        };

        List<List<K>> result = new ArrayList<>();

        for (K ts : list) {
            result.add(Arrays.asList(ts));
        }

        for (int i = 1; i < n; i += 1) {
            result = go.apply(list, result);
        }
        return result;
    }
}


