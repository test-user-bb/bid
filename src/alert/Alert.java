package alert;

import user.User;
import bid.Bid;

public abstract class Alert {

	User user;
	Bid bid;
	AlertType alertType;
	
	public Alert(User user, Bid bid, AlertType alertType) {
		this.user = user;
		this.bid = bid;
		this.alertType = alertType;
	}

	public abstract boolean trigger ();
	
	public boolean equals(Alert alert){
		if (alert.getBid() == bid && alert.getUser() == user &&
				alert.getType() == alertType){
			return true;
		}
		return false;
	}
	
	public void sendMessage(String mess) {
		user.sendMessage(mess);
	}

	public User getUser() {
		return user;
	}

	public Bid getBid() {
		return bid;
	}

	public AlertType getType() {
		return alertType;
	}
}