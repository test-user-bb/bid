package user;


import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

import bid.Bid;
import bid.BidManager;
import bid.BidState;
import bid.Clock;
import bid.Item;
import bid.Offer;
import alert.Alert;
import alert.AlertManager;
import alert.AlertType;

public class User {

	private String login;
	private String lastName;
	private String firstName;
	//create stack to stock alert
	private Stack<String> messages = new Stack<String>(); 
	
	// constructor
	public User(String login, String lastName, String firstName) {

		this.login = login;
		this.lastName = lastName;
		this.firstName = firstName;
	}

	
	// create a bid without any reservedPrice
	public boolean createBid (Item item, int nbDay, float minPrice)
	{
		if(item != null && nbDay > 0 && minPrice >= 0)
		{
			Date deadLine = new Date (Clock.getSingleton().getTime() + 1000 * 3600 * 24 * nbDay);

			Bid newBid = new Bid(item, deadLine, BidState.CREATED, minPrice, minPrice, this);
			
			if(BidManager.getInstance().addBid(newBid)) {
				return this.createAlert(newBid, AlertType.SELLER);
			}
		}
		
		return false;
	}

	// create a bid with a reservedPrice
	public boolean createBid (Item item, int nbDay, float minPrice, float reservedPrice)
	{
		// checks validity of parameters
		if(item != null && nbDay > 0 && minPrice >= 0 && reservedPrice >= minPrice)
		{
			Date deadLine = new Date (Clock.getSingleton().getTime() + 1000 * 3600 * 24 * nbDay);
			
			Bid newBid = new Bid(item, deadLine, BidState.CREATED, minPrice, reservedPrice, this);
			
			if(BidManager.getInstance().addBid(newBid))
				return this.createAlert(newBid, AlertType.SELLER);
		}
		
		return false;
	}
	
	
	
	// removes a bid
	public boolean cancelBid (Bid bid)
	{
		return bid.setState(BidState.CANCELED, this);
	}
	
	// publish a bid
	public boolean publishBid (Bid bid)
	{
		return bid.setState(BidState.PUBLISHED, this);
	}
	
	// unpublish a bid = hide a bid
	public boolean hideBid (Bid bid)
	{
		return bid.setState(BidState.CREATED, this);
	}
	
	// change the reservedPrice of a bid
	public boolean setReservedPrice (float reservedPrice, Bid bid)
	{
		return bid.setReservedPrice(reservedPrice, this);
	}
	
	// change the minPrice of a bid
	public boolean setMinPrice (float minPrice, Bid bid)
	{
		return bid.setMinPrice(minPrice, this);
	}
	
	// make an offer on a bid
	public boolean makeOffer (Bid bid, float price)
	{
		if (bid == null)
			return false;
		// checks if the user is not the bid's owner
		ArrayList <Bid> ownedBids = BidManager.getInstance().getOwnedBids(this); 
		for(Bid ownedBid : ownedBids)
		{
			if(ownedBid == bid)
				return false;
		}
		// if not, checks validity of parameters
		if(bid.getState(this) == BidState.PUBLISHED && 
				price > bid.getMinPrice() && 
				bid.getDeadLine(this).getTime() > Clock.getSingleton().getTime())
		{
			if (bid.getBestOffer(this) == null){
				Offer newOffer = new Offer(this, price, bid);
				
				// Trigger for the seller
				Alert alert = AlertManager.getInstance()
						.getAlert(bid.getSeller(this), bid, AlertType.SELLER);
				
				if (alert != null)
					alert.trigger();
				
				if(newOffer != null)
					return true;
			}
			else if (price > bid.getBestOffer(this).getPrice()){
				
				Offer oldOffer = bid.getBestOffer(this);
				Offer newOffer = new Offer(this, price, bid);
				
				if(newOffer != null) {
					
					// Trigger for the old buyer if he has been an alert
					Alert alert = AlertManager.getInstance()
							.getAlert(oldOffer.getUser(), bid, AlertType.OUTBIDED);
					
					if (alert != null)
						alert.trigger();
					
					// Trigger for the seller
					alert = AlertManager.getInstance()
							.getAlert(bid.getSeller(this), bid, AlertType.SELLER);

					if (alert != null)
						alert.trigger();
					
					return true;
				}
			}
		}
		return false;
	}
	
	public ArrayList <Alert> getOwnedAlerts() {
		return AlertManager.getInstance().getAlerts(this);
	}


	// creates an alert on a bid
	public boolean createAlert (Bid bid, AlertType alertType)
	{
		return AlertManager.getInstance().addAlert(this, bid, alertType);
	}
	
	public boolean sendMessage(String mess) {
		messages.push(mess);

		return true;
	}
	
	public boolean readMessage() {
		System.out.println(messages.pop());

		return true;
	}
	
	public int getNumberMessage() {
		return messages.size();
	}
	
	public ArrayList <Bid> getOwnedBids () {
		return BidManager.getInstance().getOwnedBids(this);
	}
	
	public ArrayList <Bid> getPublishedBids () {
		return BidManager.getInstance().getPublishedBids();
	}

	public void cancelBids() {
		ArrayList <Bid> Ownedbids = this.getOwnedBids();
		
		for (Bid Ownedbid: Ownedbids){
			this.cancelBid(Ownedbid);
		}
	}

	public boolean cancelAlert(Bid bid, AlertType type) {
		return AlertManager.getInstance().deleteAlert (this, bid, type);
	}
	
	public void clearMessages () {
		this.messages.clear();
	}
}
