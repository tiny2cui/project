
package com.simit.video.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.simit.video.test.FrameType;
import com.simit.video.test.SendAndReceive;

import android.os.Handler;
import android.util.Log;

/**
 * Implementation of a subset of the RTSP protocol (RFC 2326) This allow remote
 * control of an android device cameras & microphone For each connected client,
 * a Session is instantiated The Session will start or stop streams according to
 * what the client wants
 */
public class RtspServer {

	private final static String TAG = RtspServer.class.getName();
	private static RtspServer rtspServer;

	// Message types for UI thread
	public static final int MESSAGE_LOG = 2;
	public static final int MESSAGE_ERROR = 6;

	private  Handler handler;
	private  int port;
	private WorkerThread work;
	private SendAndReceive send;
	// private RequestListenerThread listenerThread;

	public static RtspServer getInstance(){
		if(rtspServer==null){
			rtspServer=new RtspServer();
		}
		return rtspServer;
	}
	
	public RtspServer(){
		
	}
	
	public void init(int port, Handler handler,SendAndReceive send) {
		this.handler = handler;
		this.port = port;
		this.send=send;
	}

	public void start() {
		work =new WorkerThread(port,handler,send);	
	}
	
	public void handMessage(byte[] data){
		if(work!=null){
			work.receiveData(data);
		}	
	}

	public void stop() {
//		int i=0;
		if(work.session!=null){
			work.session.stopAll();
			work.session.flush();
		}
		
	}
	
	// One thread per client
	 class WorkerThread  {

		private BufferedReader input;
		private final Handler handler;
		private String ip="192.168.1.1";
		private int port=1230;
		
		public Session session;
		private SendAndReceive send;
		public WorkerThread(int port,final Handler handler,SendAndReceive send)  {
			this.handler = handler;
			this.send=send;
			if(session==null){
			session = new  Session(null, handler);
			session.setSender(send);
			}

		}

		public void receiveData(byte[] data){
			Request request;
			Response response;
			try {
				input = new BufferedReader(new InputStreamReader(
						new ByteArrayInputStream(data)));
				// Parse the request
				request = Request.parseRequest(input);
				// Do something accordingly
				response = processRequest(request);
				// Send response
				//response.send(server, datagram);
				send.sendData(response.getSendData(), FrameType.MESSAGE_VIDEO_CONTROL_S2C);

			} catch (IllegalStateException e1) {
				loge("Client sent a bad request !");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public Response processRequest(Request request)
				throws IllegalStateException, IOException {
			Response response = new Response(request);

			/* ********************************************************************************** */
			/*
			 *  ********************************* Method DESCRIBE
			 * ********************************
			 */
			/* ********************************************************************************** */
			if (request.method.toUpperCase().equals("DESCRIBE")) {

				// Parse the requested URI and configure the session
				UriParser.parse(request.uri, session);
				String requestContent = session.getSessionDescriptor();
				String requestAttributes = 
						//"Content-Base: "+ ip + ":"+ port + "/\r\n"+ 
						"Content-Type: application/sdp\r\n";

				response.status = Response.STATUS_OK;
				response.attributes = requestAttributes;
				response.content = requestContent;

			}

			/* ********************************************************************************** */
			/*
			 *  ********************************* Method OPTIONS
			 * *********************************
			 */
			/* ********************************************************************************** */
			else if (request.method.toUpperCase().equals("OPTIONS")) {
				response.status = Response.STATUS_OK;
				response.attributes = "Public: DESCRIBE,SETUP,TEARDOWN,PLAY,PAUSE\r\n";
			}

			/* ********************************************************************************** */
			/*
			 *  ********************************** Method SETUP
			 * **********************************
			 */
			/* ********************************************************************************** */
			else if (request.method.toUpperCase().equals("SETUP")) {
				Pattern p;
				Matcher m;
				int p2, p1, ssrc, trackId, src;

				p = Pattern.compile("trackID=(\\w+)", Pattern.CASE_INSENSITIVE);
				m = p.matcher(request.uri);

				if (!m.find()) {
					response.status = Response.STATUS_BAD_REQUEST;
					return response;
				}

				trackId = Integer.parseInt(m.group(1));

				if (!session.trackExists(trackId)) {
					response.status = Response.STATUS_NOT_FOUND;
					return response;
				}

//				p = Pattern.compile("client_port=(\\d+)-(\\d+)",
//						Pattern.CASE_INSENSITIVE);
//				m = p.matcher(request.headers.get("Transport"));
//
//				if (!m.find()) {
//					int port = session.getTrackDestinationPort(trackId);
//					p1 = port;
//					p2 = port + 1;
//				} else {
//					p1 = Integer.parseInt(m.group(1));
//					p2 = Integer.parseInt(m.group(2));
//				}

				ssrc = session.getTrackSSRC(trackId);
				src = session.getTrackLocalPort(trackId);
				//session.setTrackDestinationPort(trackId, p1);
				

				try {
					session.start(trackId);
					response.attributes = "Transport: RTP/AVP/UDP;unicast;" 
//							"client_port="
//							+ p1
//							+ "-"
//							+ p2
//							+ ";server_port="
//							+ src
//							+ "-"
//							+ (src + 1)
							+ "ssrc="
							+ Integer.toHexString(ssrc)
							+ ";mode=play\r\n"
							+ "Session: "
							+ "1185d20035702ca"
							+ "\r\n"
							+ "Cache-Control: no-cache\r\n";
					response.status = Response.STATUS_OK;
				} catch (RuntimeException e) {
					response.status = Response.STATUS_INTERNAL_SERVER_ERROR;
					throw new RuntimeException(
							"Could not start stream, configuration probably not supported by phone");
				}

			}

			/* ********************************************************************************** */
			/*
			 *  ********************************** Method PLAY
			 * ***********************************
			 */
			/* ********************************************************************************** */
			else if (request.method.toUpperCase().equals("PLAY")) {
				String requestAttributes = "RTP-Info: ";
				if (session.trackExists(0))
					requestAttributes += //"url=rtsp://" + ip+ ":" + port + 
							"*/trackID=" + 0
							+ ";seq=0,";
				if (session.trackExists(1))
					requestAttributes += //"url=rtsp://" + ip + ":" + port + 
					"*/trackID=" + 1 + ";seq=0,";
				requestAttributes = requestAttributes.substring(0,
						requestAttributes.length() - 1)
						+ "\r\nSession: 1185d20035702ca\r\n";

				response.status = Response.STATUS_OK;
				response.attributes = requestAttributes;
			}

			/* ********************************************************************************** */
			/*
			 *  ********************************** Method PAUSE
			 * **********************************
			 */
			/* ********************************************************************************** */
			else if (request.method.toUpperCase().equals("PAUSE")) {
				response.status = Response.STATUS_OK;
			}

			/* ********************************************************************************** */
			/*
			 *  ********************************* Method TEARDOWN
			 * ********************************
			 */
			/* ********************************************************************************** */
			else if (request.method.toUpperCase().equals("TEARDOWN")) {
				response.status = Response.STATUS_OK;
				session.stopAll();
				session.flush();
				session=null;
			}

			/* Method Unknown */
			else {
				Log.e(TAG, "Command unknown: " + request);
				response.status = Response.STATUS_BAD_REQUEST;
			}

			return response;

		}

		private void log(String message) {
			handler.obtainMessage(MESSAGE_LOG, message).sendToTarget();
			Log.v(TAG, message);
		}

		// Display an error on user interface
		private void loge(String error) {
			handler.obtainMessage(MESSAGE_LOG, error).sendToTarget();
			Log.e(TAG, error);
		}

	}

	static class Request {

		// Parse method & uri
		public static final Pattern regexMethod = Pattern.compile(
				"(\\w+) (\\S+) RTSP", Pattern.CASE_INSENSITIVE);
		// Parse a request header
		public static final Pattern rexegHeader = Pattern.compile(
				"(\\S+):(.+)", Pattern.CASE_INSENSITIVE);

		public String method;
		public String uri;
		public int Cseq = 0;
		public HashMap<String, String> headers = new HashMap<String, String>();

		/** Parse the method, uri & headers of a RTSP request */
		public static Request parseRequest(BufferedReader input)
				throws IOException, IllegalStateException, SocketException {
			Request request = new Request();
			String line;
			Matcher matcher;

			// Parsing request method & uri
			if ((line = input.readLine()) == null)
				throw new SocketException("Client disconnected");
			matcher = regexMethod.matcher(line);
			matcher.find();
			request.method = matcher.group(1);
			request.uri = matcher.group(2);

			// Parsing headers of the request
			while ((line = input.readLine()) != null && line.length() > 3) {
				matcher = rexegHeader.matcher(line);
				matcher.find();
				System.out.println(matcher.group());
				
				request.headers.put(matcher.group(1), matcher.group(2));
			}
			if (line == null)
				//throw new SocketException("Client disconnected");
			Log.e(TAG, request.method + " " + request.uri);
			return request;
		}
	}

	static class Response {

		// Status code definitions
		public static final String STATUS_OK = "200 OK";
		public static final String STATUS_BAD_REQUEST = "400 Bad Request";
		public static final String STATUS_NOT_FOUND = "404 Not Found";
		public static final String STATUS_INTERNAL_SERVER_ERROR = "500 Internal Server Error";

		public String status = STATUS_OK;
		public String content = "";
		public String attributes = "";
		public String cseq = "";
		private final Request request;

		public Response(Request request) {
			this.request = request;
		}
		
		public byte[] getSendData(){
			int seqid = -1;

			try {
				String strcseq = request.headers.get("CSeq");
				strcseq = strcseq.trim();
				seqid = Integer.parseInt(strcseq);

			} catch (Exception ignore) {
			}

			String response = "RTSP/1.0 " + status + "\r\n"
					//+ "Server: MajorKernelPanic RTSP Server\r\n"
					+ (seqid >= 0 ? ("Cseq: " + seqid + "\r\n") : "")
					+ "Content-Length: " + content.length() + "\r\n" + cseq
					+ attributes + "\r\n" + content;

			Log.d(TAG, response);
			return response.getBytes();
		}

//		public void send(final DatagramSocket server,
//				final DatagramPacket datagram) throws IOException {
//			int seqid = -1;
//
//			try {
//				String strcseq = request.headers.get("CSeq");
//				strcseq = strcseq.trim();
//				seqid = Integer.parseInt(strcseq);
//
//			} catch (Exception ignore) {
//			}
//
//			String response = "RTSP/1.0 " + status + "\r\n"
//					+ "Server: MajorKernelPanic RTSP Server\r\n"
//					+ (seqid >= 0 ? ("Cseq: " + seqid + "\r\n") : "")
//					+ "Content-Length: " + content.length() + "\r\n" + cseq
//					+ attributes + "\r\n" + content;
//
//			Log.d(TAG, response);
//			//TODO 添加报文头
//			datagram.setData(response.getBytes());
//			datagram.setLength(response.getBytes().length);
//			//TODO 设置服务器端口
//			datagram.setPort(8096);
//			server.send(datagram);
//		}
	}

}
