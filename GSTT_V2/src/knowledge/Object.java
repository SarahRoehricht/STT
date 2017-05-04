package knowledge;



public class Object {

private String name;
private String pluralName;

private String color;
private String location;
private int size;
private int weight;
private int count;
private String category;
public Object(String name, String color,String location, int size, int weight, int count, String category,String pluralName){
	this.name=name;
	this.color=color;
	this.location=location;
	this.size=size;
	this.weight=weight;
	this.count=count;
	this.category=category;
	this.pluralName=pluralName;
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
public String getCategory() {
	return category;
}	
public String getPluralName() {
	return pluralName;
}
@Override
	public String toString() {
		
	if(this.count==1){
		String rtn="a "+this.color + " "+this.name+" which is " + this.location+ ". " + this.getSize() + " centimetres tall, weighs " + this.getWeight()+" grams, belongs to the category "+this.getCategory()+ " and there is "+ this.getCount() +" of it.";
		return rtn;
		
	}else{
		String rtn="the "+this.color + " "+this.name+" which is " + this.location+ ". " + this.getSize() + " centimetres tall, weighs " + this.getWeight()+" grams , belongs to the category "+this.getCategory()+", and there are "+ this.getCount() +" of them.";
		return rtn;
	}
	
	  
				
				
	}
}
