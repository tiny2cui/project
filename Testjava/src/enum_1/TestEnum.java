package enum_1;

public class TestEnum {
//	private static int TURN_LEFT;
	public static void main(String[] args)   
    {  
        doAction(Action.TURN_RIGHT);  
    }  
  
    public static void doAction(Action action)  
    {  
        switch(action)  
        {  
            case TURN_LEFT:  
                System.out.println("向左转"+action.getStr());  
                break;  
            case TURN_RIGHT:  
                System.out.println("向右转");  
                break;  
            case SHOOT:  
                System.out.println("射击");  
                break;  
        }  
    }  

}
