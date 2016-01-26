class A {
    int i;
}

class T extends A{
    int y;
    void f() {
	    // }
	    System.out.println("T:f");
    }
}
T t = new T();
t.f();

