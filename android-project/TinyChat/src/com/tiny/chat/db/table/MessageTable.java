package com.tiny.chat.db.table;

public class MessageTable {
	private MessageTable() {

	}

	/**
	 * table name
	 * <P>
	 * Type: TEXT
	 * </P>
	 */
	public static final String TABLE_NAME = "message";

	/**
	 * Primary Key
	 * <P>
	 * Type: INTEGER
	 * </P>
	 */
	public static final String ID_MESSAGE = "idMessage";

	/**
	 * conversation id
	 * <P>
	 * Type: INTEGER
	 * </P>
	 */
	public static final String CONVERSATION_ID = "conversationID";
	
	/**
	 * send id
	 * <P>
	 * Type: INTEGER
	 * </P>
	 */
	public static final String SEND_ID = "sendID";

	/**
	 * receive id
	 * <P>
	 * Type: INTEGER
	 * </P>
	 */
	public static final String RECEIVE_ID = "receiveID";

	/**
	 * message type
	 * <P>
	 * Type: INTEGER
	 * </P>
	 */
	public static final String MESSAGE_TYPE = "messageType";

	/**
	 * content
	 * <P>
	 * Type: TEXT
	 * </P>
	 */
	public static final String CONTENT = "content";

	/**
	 * date
	 * <P>
	 * Type: TEXT
	 * </P>
	 */
	public static final String DATE = "date";

	/**
	 * message state
	 * <P>
	 * Type: INTEGER
	 * </P>
	 */
	public static final String MESSAGE_STATE = "messageState";
}
