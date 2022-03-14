package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> cmp;

    public MaxArrayDeque(Comparator<T> c) {
        cmp = c;
    }

    public T max() {
        if (size() == 0) {
            return null;
        }
        T maximum = get(0);
        for (int i = 0; i < size(); i += 1) {
            if (this.cmp.compare(maximum, get(i)) < 0) {
                maximum = get(i);
            }
        }
        return maximum;
    }

    public T max(Comparator<T> c) {
        Comparator<T> tmp = cmp;
        cmp = c;
        T cMax = max();
        cmp = tmp;
        return cMax;
    }
}
