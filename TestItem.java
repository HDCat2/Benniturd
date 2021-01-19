import java.util.*;

public class TestItem{
	public static void main(String[]deadinside){
		Player player = new Player("knight");
		Item i = new Item(1,player);
		Item p = new Item(50,player);
		Item c = new Item(100,player);
		System.out.println(i.getName() + " " + i.getRating());
		System.out.println(p.getName() + " " + p.getRating());
		System.out.println(c.getName() + " " + c.getRating());
	}
}