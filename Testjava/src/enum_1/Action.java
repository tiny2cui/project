package enum_1;

public enum Action {
	TURN_LEFT("123456"),  
    TURN_RIGHT("12345"),  
    SHOOT("1234");
	private String description;
	private Action(String str){
		this.description=str;
	}
	public String getStr(){
		return this.description;
	}
}
