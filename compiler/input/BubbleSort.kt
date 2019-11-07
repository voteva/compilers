class BubbleSort {
    public static fun main(String[] args) {
        bool success;
        success = new BBS().start(10);
    }
}


// This class contains the array of integers and also
// methods to initialize, print and sort the array using Bublesort

class BBS {

    int[] numbers;
    int size;

    public bool start(int sz) {
        int res;

        res = this.init(sz);
        res = this.print();

        println(99999);

        res = this.sort();
        res = this.print();

        return true;
    }

    int sort() {
        int nt;
        int i;
        int aux02;
        int aux04;
        int aux05;
        int aux06;
        int aux07;
        int j;
        int t;
        i = size - 1;
        aux02 = 0 - 1;
        while (aux02 < i) {
            j = 1;
            while (j < (i + 1)) {
                aux07 = j - 1;
                aux04 = numbers[aux07];
                aux05 = numbers[j];
                if (aux05 < aux04) {
                    aux06 = j - 1;
                    t = numbers[aux06];
                    numbers[aux06] = numbers[j];
                    numbers[j] = t;
                } else nt = 0;
                j = j + 1;
            }
            i = i - 1;
        }
        return 0;
    }

    int print() {
        int j;
        j = 0;
        while (j < (size)) {
            println(numbers[j]);
            j = j + 1;
        }
        return 0;
    }

    int init(int sz) {
        size = sz;
        numbers = new int[sz];

        numbers[0] = 20;
        numbers[1] = 7;
        numbers[2] = 12;
        numbers[3] = 18;
        numbers[4] = 2;
        numbers[5] = 11;
        numbers[6] = 6;
        numbers[7] = 9;
        numbers[8] = 19;
        numbers[9] = 5;

        return 0;
    }
}
