//Created by Magdalen McCulloch
/*This program delivers a personalized order of Rolling Stones top 500 albums for each user. 
 * First, you complete a quiz about your music taste. Based on your responses, the program assigns a value to each album 
 * and adds them to a linked list in an ordered fashion. Then, it prints this linked list. 
 * You can run it in any java environment. Be sure to download "albumlist.csv" and save it in the same folder. 
 *  
 * 
 */
import java.io.*;  
import java.util.Scanner;   
public class linkListPractice {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner scanInt = new Scanner(System.in); 
		Scanner scanLine = new Scanner(System.in); 
		int decade = 0; 
		int dislikeDecade = 0; 
		String genre = null; 
		String subGenre = null; 
		String newGenre = null; 
		
		System.out.println("take this quiz to get a personalized ordering of Rolling Stone's Top 500 Albums"); 
		
		System.out.println("Enter your favorite decade of music:"); 
		decade = scanInt.nextInt(); 
		System.out.println("Enter your least favorite decade of music:"); 
		dislikeDecade = scanInt.nextInt(); 
		System.out.println("What is your favorite genre?"); 
		genre = scanLine.nextLine();
		System.out.println("What is your second favorite genre?"); 
		subGenre = scanLine.nextLine(); 
		System.out.println("What genre do you want to listen to more?"); 
		newGenre = scanLine.nextLine();  
		
		
		scanInt.close();
		scanLine.close();   
		linkListPractice myList = new linkListPractice ();
		
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader("C:\\Users\\magda\\eclipse-workspace\\SummerProject\\albumlist.csv"));
				 String currLine = null; 
				 int currPoints = 0; 
		 String delimiter = ","; 
		int myIndex = 0; 
		//must read the first line of the csv before the loop
		currLine = reader.readLine(); 
		while((currLine = reader.readLine()) != null) {
			String[] currColumn = currLine.split(delimiter);
			//System.out.println(currColumn[3]);
			int currRank = Integer.parseInt(currColumn[0]);  
			int currYear = Integer.parseInt(currColumn[1]); 
			//genre
			String currGenre = currColumn[4].toLowerCase(); 
			currGenre = currGenre.replaceAll("[^a-zA-Z0-9]", ""); 
			//subgenre
			String currSubGenre = currColumn[5].toLowerCase();
			currSubGenre = currSubGenre.replaceAll("[^a-zA-Z0-9]", ""); 
	
			if( (currYear- decade) <= 10) {
				currPoints++;  
			}
			if(currGenre.contains(genre.toLowerCase()) || currGenre.contains(newGenre.toLowerCase())) {
				currPoints += 2;  
			}
			if((subGenre!= null)&&currSubGenre.contains(subGenre.toLowerCase()) || currSubGenre.contains(genre.toLowerCase())) {
				currPoints++; 
			}
			//this one must be at the end of the points changers
			if( ((currYear- dislikeDecade) <= 10) &&(currPoints>0)) {
				currPoints--;  
			}
		//insert it in the right spot
			myList.addAtPoints(currRank, currYear,currColumn[2],currColumn[3],currColumn[4],currColumn[5], currPoints); 
			currPoints = 0; 
		} 
		printList(myList);  
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} //main

	class Node{
		int ranking = 0; 
		int releaseYear = 0; 
		String title = null; 
		String band = null; 
		String genre = null; 
		String subgenre = null; 
		int points = 0; //this is what we will update!
		Node next = null; //reference to next node 
		
		Node(int ranking, int releaseYear, String title, String band, String genre, String subgenre, int points) { 
			this.ranking = ranking; 
			this.releaseYear = releaseYear;
			this.title = title; 
			this.band = band; 
			this.genre = genre; 
			this.subgenre = subgenre; 
			this.points = points; 
			this.next = null; 
		}
	}
	protected static Node head = null;
	protected Node tail  = null; 
	
	public void addToFront ( int ranking, int releaseYear, String title, String band, String genre, String subgenre, int points) {
		Node newNode = new Node(ranking, releaseYear, title, band, genre, subgenre, points); 
		newNode.next = head; 
		head = newNode; 
		if (newNode.next == null) {
			tail = newNode; 
		}
	} // add to front 
	public void addToBack (int ranking, int releaseYear, String title, String band, String genre, String subgenre, int points) {
		Node newNode = new Node(ranking, releaseYear, title, band, genre, subgenre, points);  
		if(tail == null) {
			head = newNode; 
		}
		else {
			tail.next = newNode;
		}
		tail = newNode; 
	} // add to back 
	public void addAtIndex (int index,int ranking, int releaseYear, String title, String band, String genre, String subgenre, int points) {
		if(index < 0) {
			throw new IndexOutOfBoundsException(); 
		} 
		else if (index == 0) {
			addToFront(ranking, releaseYear, title, band, genre, subgenre, points); 
		}
		else {
			Node newNode = new Node(ranking, releaseYear, title, band, genre, subgenre, points); 
			Node current = head; 
			for(int i = 0; i < index -1; i++) {
				if(current == null) {
					throw new IndexOutOfBoundsException(); 
				}
				current = current.next; 
			}
			if (current.next == null) {
				tail = newNode; 
			}
			else {
			newNode.next = current.next; 
			current.next = newNode; 
			} 
		}
	}//add at index
	public void addAtPoints ( int ranking, int releaseYear, String title, String band, String genre, String subgenre, int points) {
		Node decard = new Node(ranking, releaseYear, title, band, genre, subgenre, points); 
		Node current = head; 
		//error handling 
		if(ranking <= 500) {
			//case 1
			if(points < 0) {
				throw new IndexOutOfBoundsException(); 
			} 
			//case 2
			else if (points == 0) {
				tail = decard; 
				addToBack(ranking, releaseYear, title, band, genre, subgenre, points); 
			}
			//case 3
			//this new node has the greatest amount of points in the list so far
			//which includes the case that there is only one thing in the list
			else if ((current== null)||(head == null) || (decard.points > current.points)) {
				addToFront(ranking, releaseYear, title, band, genre, subgenre, points); 
			}
			//case 4
			else {
				while((current.next != null)&&(current.points>decard.points)) {
					current = current.next;  
				}
					decard.next = current.next; 
					current.next = decard; 
			}
				/* 
				if(head == null) {
					head = decard; 
				}
				Node current = head; 
				//for(int i = 0; i < points -1; i++) {
					while(current.points> points) {
						current = current.next; 
					}
					if (current.next == null) {
						tail = decard; 
					}
					else {
					decard.next = current.next; 
					current.next = decard; 
					} 
				*/ 
		}
	}//add at points
	/* for practice 
	public boolean contains(int value) {
		Node current = head; 
		while (current != null) {
			if (current.value == value) {
				return true; 
			}
			current = current.next; 
		}
		return false; 
	} // contains 
	*/ 
	
	public int getByIndex(int index) {
		if(index < 0) {
			throw new IndexOutOfBoundsException(); 
		}  
		Node current = head; 
		for(int i = 0; i <index; i++) {
			if(current == null || current.next == null) {
				throw new IndexOutOfBoundsException(); 
			}  
			current = current.next; 
		}
		return current.ranking; 
		//return current.releaseYear; 
	} // get by index 
	public void removeFromFront() {
		if(head != null) {
			head = head.next; 
		}
		if (head == null) {
			tail = null; 
		}
	} // remove from front 
	public void removeFromBack() {
		if (head == null) { //empty list
			return; 
		}
		else if (head == tail) { // one element in list
			head = null;
			tail = null; 
		}
		else {
			Node current = head; 
			while (current.next != tail) {
				current = current.next; 
			}
			tail = current; 
			current.next = null; 
		} 
	} // remove from back
	public void removeAtIndex (int index) {
		if(index < 0) {
			throw new IndexOutOfBoundsException(); 
		} 
		else if (index == 0) {
			removeFromFront(); 
		}
		else {
			Node current = head; 
			for (int i = 0; i <index -1; i++) {
				if(current == null) {
					throw new IndexOutOfBoundsException(); 
				} 
				current = current.next; 
			}
			current.next = current.next.next; 
			if (current.next == null) {
				tail = current; 
			}
		}
	} // remove at index 
	//make sure to call this in add at points 
	public static void printList( linkListPractice listy) {
		System.out.println("points\t"+"ranking\t"+"release year\t"+"album\t"+"band\t"+"genre\t"+"subgenre\t");
		Node curr = head; 
		if(head == null) {
			System.out.println("the list is empty"); 
		}
		while (curr != null) {
		//for(int i = 0; i < 20; i++) {
			System.out.print(curr.points  +"\t"+ curr.ranking +"\t"+ curr.releaseYear +"\t"+ curr.title +"\t"
		+ curr.band +"\t"+ curr.genre +"\t"+ curr.subgenre); 
			curr = curr.next; 
			System.out.println(); 
		}  
		
	} // printList 
 
}//linked list practice 
