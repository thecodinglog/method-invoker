package learning;

/**
 * @author Jeongjin Kim
 * @since 2021-03-09
 */
class OverloadedMethods {
    public void hello() {
        System.out.println("no arg");
    }

    public void hello(String arg) {
        System.out.println("one string arg");
    }

    public void hello(Integer arg) {
        System.out.println("one integer arg");
    }

    public void hello(String arg0, String arg1) {
        System.out.println("two string args");
    }
}
