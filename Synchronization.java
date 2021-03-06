
class MovieTickets{
	
	String name;
	int price;
	char row;
	int seatNumber;
	
	boolean isBooked;
	String userEmail;
	
	MovieTickets(){
		isBooked = false;
		userEmail = "NA";
	}

	MovieTickets(String name, int price, char row, int seatNumber) {
		this();
		this.name = name;
		this.price = price;
		this.row = row;
		this.seatNumber = seatNumber;
	}

	@Override
	public String toString() {
		return "MovieTicket [name=" + name + ", price=" + price + ", row=" + row + ", seatNumber=" + seatNumber
				+ ", isBooked=" + isBooked + ", userEmail=" + userEmail + "]";
	}
	
}

class MovieUser{
	
	String name;
	String email;
	boolean isPrime;
	
	MovieUser(){
		isPrime = false;
	}

	MovieUser(String name, String email) {
		this();
		this.name = name;
		this.email = email;
	}
	
	void upgradeToPrime() {
		isPrime = true;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", email=" + email + "]";
	}
	
}

// BookMovieTicketTask is a Thread
class BookMovieTicketTask extends Thread{
	
	MovieTickets ticket;
	MovieUser user;
	
	BookMovieTicketTask(MovieTickets ticket, MovieUser user) {
		this.ticket = ticket;
		this.user = user;
	}
	
	public void run() {
		
		// whenever, thread will execute run method, a lock will be acquired
		// locking happens on ticket object which means, no other thread can access the ticket object
		synchronized (ticket) {
			
			if(!user.isPrime) {
				try {
					System.out.println("Dear, "+user.name+" Please Wait...");
					ticket.wait(); // makes the thread to be blocked for infinite time
					//ticket.wait(1000); // causing thread to wait for 1 second
					// lock will be released here so that other thread can acquire it
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			if(!ticket.isBooked) {
				
				// 1. Pay for Ticket
				System.out.println("Dear, "+user.name+".Please Pay: "+ticket.price);
				System.out.println("Transaction Started for "+user.name);
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Transaction Finished for "+user.name);
				
				// 2. Movie Ticket will have user details added to it
				System.out.println("Allocating Seat "+ticket.seatNumber+" to "+user.name);
				ticket.isBooked = true;
				ticket.userEmail = user.email;
				
				// 3. Send Email to User as acknowledgement
				System.out.println("Email Sent for Booked Ticket to email "+user.email);
				
				ticket.notify(); // whosoever is waiting to book the ticket can be notified to resume its execution
				//ticket.notifyAll(); // in case multiple threads are waiting
			}else {
				System.err.println("Dear, "+user.name+" seat number: "+ticket.seatNumber+" is already booked");
			}
		} // when synchronized block finishes here, then lock will be released automatically
		
	}
}

// Synchronization is needed when multiple threads work on the same object

class CA{
	
	// synchronized method is a method, if it is executed by 1 thread, other thread has to wait
	synchronized void book() {
		
	}
}

public class Synchronization {

	public static void main(String[] args) {
		
		MovieTickets[] ticketsInRowN = new MovieTickets[12];
		
		for(int i=0;i<ticketsInRowN.length;i++) {
			ticketsInRowN[i] = new MovieTickets("Pushpa", 200, 'N', i+1); // i+1 is for set so that it starts from 1
		}
		
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("MOVIE TICKETS IN ROW N BEFORE");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		for(MovieTickets ticket : ticketsInRowN) {
			System.out.println(ticket);
		}
		System.out.println();
		
		MovieUser user1 = new MovieUser("John", "john@example.com");
		MovieUser user2 = new MovieUser("Fionna", "fionna@example.com");
		user2.upgradeToPrime(); // Fionna is a Prime User :)
		
		// 0. Select the Movie Ticket
		// As of now, both the tasks are working on separate movie ticket objects :)
		//BookMovieTicketTask task1 = new BookMovieTicketTask(ticketsInRowN[1], user1); // seat #2
		//BookMovieTicketTask task2 = new BookMovieTicketTask(ticketsInRowN[9], user2); // seat #10
		
		// 0. Select the Movie Ticket
		// Now here, both the Users wish to buy the same movie ticket :)
		// Here the threads are accessing the same object and hence this is an algorithmic challenge
		BookMovieTicketTask task1 = new BookMovieTicketTask(ticketsInRowN[6], user1); // seat #7
		BookMovieTicketTask task2 = new BookMovieTicketTask(ticketsInRowN[6], user2); // seat #7
		
		task1.start();
		task2.start();
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println();
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		System.out.println("MOVIE TICKETS IN ROW N AFTER");
		System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		for(MovieTickets ticket : ticketsInRowN) {
			System.out.println(ticket);
		}
		

	}

}

//Deadlock -> where 2 threads are waiting for each other to be notified

