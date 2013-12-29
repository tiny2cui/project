package enum_1;

public class Assign {

	public static void main(String[] args) {
		getBestValue();

	}
	public static void getBestValue(){
		int num=1000;
		System.out.println("num="+num);
		for(int a=0;a<=60;a++){
			for(int b=0;b<=70;b++){
				for(int c=0;c<=60;c++){
					for(int d=0;d<=50;d++){
						for(int e=0;e<=20;e++){
							for(int f=0;f<=30;f++){
								if(a+f>=60&&a+b>=70&&b+c>=60&&c+d>=50&&d+e>=20&&e+f>=30){
									if(num>a+b+c+d+e+f){
										num=a+b+c+d+e+f;
										System.out.println("a="+a+"b="+b+"c="+c+"d="+d+"e="+e+"f="+f+"num="+num);
									}
									
								}
							}	
						}	
					}
				}
			}
		}
	}

}
