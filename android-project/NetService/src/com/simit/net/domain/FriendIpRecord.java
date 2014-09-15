package com.simit.net.domain;

import java.util.ArrayList;
import java.util.List;

import com.simit.net.domain.Friend;


/**
 * 友邻IP记录集
 * 
 * 记录友邻的ID与IP之间对应关系
 * 
 * @author admin
 *
 */
public class FriendIpRecord {

	private List<Friend>	friends;

	public FriendIpRecord(){
		friends = new ArrayList<Friend>();
	}
	
	public void addFriend(short id, String ipAddr){
		Friend		friendIdWithIp;
		deleteFriend(id);
		friendIdWithIp = new Friend(id, ipAddr);
		friends.add(friendIdWithIp);
	}
	
	public void deleteFriend(int id){
		for(int i = 0; i < friends.size(); i++){
			if(friends.get(i).getId() == id){
				friends.remove(i);
			}
		}
	}
	
	public String getIp(int id){
		for (Friend friendIdWithIp : friends) {
			if(friendIdWithIp.getId() == id){
				return friendIdWithIp.getIp();
			}
		}
		
		return null;
	}
	
	public List<Friend> getFriends() {
		return friends;
	}

	public void setFriends(List<Friend> friends) {
		this.friends = friends;
	}
	
}
