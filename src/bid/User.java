package bid;


import java.util.ArrayList;
import java.util.Date;

import alert.AlertType;

public class User {

	String login;
	String lastName;
	String firstName;
	ArrayList<Bid> ownedBids = null;
	
	// create a bid without any reservedPrice
	public Bid createBid (Item item, Date deadLine, float minPrice)
	{
		Bid newBid = new Bid(deadLine, BidState.CREATED, minPrice, minPrice, this);
		//TODO : quelle valeur pour le reservePrice � minPrice ?
		return newBid;
	}
	
	// create a bid with a reservedPrice
	public Bid createBid (Item item, Date deadLine, float minPrice, float reservedPrice)
	{
		Bid newBid = new Bid(deadLine, BidState.CREATED, minPrice, reservedPrice, this);
		return newBid;
	}
	
	// removes a bid
	public Bid cancelBid (Bid bid)
	{
		for(Bid bidToCancel : ownedBids) {
		    if(bidToCancel == bid){
		    	bidToCancel.setState(BidState.CANCELED);
		    }
		}
		return bid;
	}
	
	public Bid publishBid (Bid bid)
	{
		for(Bid bidToPublish : ownedBids) {
		    if(bidToPublish == bid){
		    	bidToPublish.setState(BidState.PUBLISHED);
		    }
		}
		return bid;
	}
	
	public Bid hideBid (Bid bid)
	{
		for(Bid bidToHide : ownedBids) {
		    if(bidToHide == bid){
		    	bidToHide.setState(BidState.CREATED);
		    }
		}
		return bid;
	}
	
	public boolean setReservedPrice (float reservedPrice, Bid bid)
	{
		for(Bid bidToEdit : ownedBids) {
		    if(bidToEdit == bid){
		    	bidToEdit.setReservedPrice(reservedPrice);
		    }
		}
		return false;
	}
	
	public boolean setMinPrice (float price)
	{
		return false;
	}
	
	public boolean makeOffer (Bid bid, float price)
	{
		return false;
	}
	
	public boolean createAlert (Bid bid, AlertType alertType)
	{
		return false;
	}
	
	public ArrayList<Bid> getOwnedBids ()
	{
		return this.ownedBids;
	}	
}
