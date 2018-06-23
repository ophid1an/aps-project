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
        List<List<K>> result = new ArrayList<>();

        for (K ts : list) {
            result.add(Arrays.asList(ts));
        }

        for (int i = 1; i < n; i += 1) {
            List<List<K>> tmpResult = new ArrayList<>();

            for (List<K> lObj : result) {
                for (K obj : list) {
                    List<K> tmpLObj = new ArrayList<>(lObj);
                    tmpLObj.add(obj);
                    tmpResult.add(tmpLObj);
                }
            }
            result = tmpResult;
        }
        return result;
    }

    static <K> List<List<K>> getNPermutations(List<K> list, int n) {
        List<List<K>> result = new ArrayList<>();

        for (K ts : list) {
            result.add(Arrays.asList(ts));
        }

        for (int i = 1; i < n; i += 1) {
            List<List<K>> tmpResult = new ArrayList<>();

            for (List<K> lObj : result) {
                for (K obj : list) {
                    List<K> tmpLObj = new ArrayList<>(lObj);
                    if (!tmpLObj.contains(obj)) {
                        tmpLObj.add(obj);
                        tmpResult.add(tmpLObj);
                    }
                }
            }
            result = tmpResult;
        }
        return result;
    }
}

