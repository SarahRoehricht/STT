package knowledge;



public class Object {

private String name;
private String color;
private String location;
private int size;
private int weight;
private int count;
public Object(String name, String color,String location, int size, int weight, int count){
	this.name=name;
	this.color=color;
	this.location=location;
	this.size=size;
	this.weight=weight;
	this.count=count;
}


public String getName() {
	
	return name;
}
public String getColor() {
	return color;
}
public String getLocation() {
	return location;
}
public int getSize() {
	return size;
}
public int getWeight() {
	return weight;
}
public int getCount() {
	return count;
}	
@Override
	public String toString() {
		
	if(this.count==1){
		String rtn="a "+this.color + " "+this.name+" which is " + this.location+ ". " + this.getSize() + " centimetres tall, weighs " + this.getWeight()+" grams, and there is "+ this.getCount() +" of it.";
		return rtn;
		
	}else{
		String rtn="a "+this.color + " "+this.name+" which is " + this.location+ "." + this.getSize() + " tall, weighs " + this.getWeight()+", and there are "+ this.getCount() +" of them.";
		return rtn;
	}
	
	  
				
				
	}
}
