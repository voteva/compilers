class ArrayTest {
    public static fun main(String[] args) {
        bool success;
        success = new Test().start(10);
    }
}

class Test {

	public bool start(int sz){
		int[] b;
		int l;
		int i;
		b = new int[sz];
		l = b.length;
		i = 0;
		while(i < (l)){
			b[i] = i;
			println(b[i]);
			i = i + 1;
		}
		return true;
	}

}
