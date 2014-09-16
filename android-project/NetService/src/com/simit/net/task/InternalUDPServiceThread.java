package com.simit.net.task;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import android.util.Log;

import com.simit.net.NetConfig;
import com.simit.net.domain.Client;
import com.simit.net.domain.ClientTypeRecord;
import com.simit.net.domain.FramePacket;
import com.simit.net.domain.LocalProperty;
import com.simit.net.domain.NetType;
import com.simit.net.service.NetService;
import com.simit.net.utils.Constants;
import com.simit.net.utils.FrameType;
import com.simit.net.utils.IPv4Util;
import com.simit.net.utils.PackageFactory;
import com.simit.net.utils.TypeConvert;

public class InternalUDPServiceThread {

	private NetService service;
	private LinkedList<DatagramPacket> receive;
	private Semaphore sync;
	private Producer producer;
	private Consumer consumer;

	public InternalUDPServiceThread(NetService netService) {
		this.service = netService;

	}

	public void start() {
		sync = new Semaphore(0);
		receive = new LinkedList<DatagramPacket>();
		producer = new Producer(receive, sync);
		consumer = new Consumer(receive, sync, service);
	}

	public void stop() {
		producer.running = false;
		consumer.running = false;
	}

	private static class Producer extends Thread implements Runnable {

		public boolean running = true;

		private final Semaphore sync;
		private final LinkedList<DatagramPacket> receivePackets;
		private DatagramSocket serverSocket;
		private DatagramPacket receivePackage;
		private byte[] buffer = new byte[1500];

		public Producer(LinkedList<DatagramPacket> receives, Semaphore sync) {
			this.receivePackets = receives;
			this.sync = sync;
			this.start();
		}

		public void run() {
			try {
				serverSocket = new DatagramSocket(Constants.INTERNAL_UDP_PORT);
				receivePackage = new DatagramPacket(buffer, buffer.length);
				while (running) {
					receivePackage.setLength(buffer.length);
					serverSocket.receive(receivePackage);
					receivePackets.add(new DatagramPacket(receivePackage
							.getData().clone(), receivePackage.getLength(),
							receivePackage.getAddress(), receivePackage
									.getPort()));
					sync.release();
				}
			} catch (IOException ignore) {
			} finally {
				running = false;
			}
		}
	}

	private static class Consumer extends Thread implements Runnable {

		private String TAG = "Consumer";
		public boolean running = true;
		private final Semaphore sync;
		private final LinkedList<DatagramPacket> receives;
		private DatagramPacket receive = null;
		private DatagramPacket sendPacket = null;
		private DatagramSocket sendSocket;
		private NetService netService;
		private byte[] buffer = new byte[1500];

		public Consumer(LinkedList<DatagramPacket> receives, Semaphore sync,
				NetService service) {
			this.receives = receives;
			this.sync = sync;
			this.netService = service;
			this.start();
		}

		public void run() {
			try {
				sendSocket = new DatagramSocket();
				sendPacket = new DatagramPacket(buffer, buffer.length);
				while (running  ) {
					sync.acquire(1);
					receive=receives.pop();					
					readDataFrame(receive);
					if(!receives.isEmpty()){						
					}					
				}
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			} 
			catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Log.d(TAG,"execute stopped !");

		}

		private void readDataFrame(DatagramPacket framePackage) {
			byte[] frameData = framePackage.getData();
			int len=framePackage.getLength();
			final byte FRAME_HEAD_FLAG_D5 = (byte) 0xD5;
			final byte FRAME_HEAD_FLAG_C8 = (byte) 0xC8;

			/*
			 * 找到帧头标志
			 */
			if (frameData == null || frameData.length < 11) { // 读入错误或者已读到最后
				return;
			}

			if ((byte) frameData[0] == FRAME_HEAD_FLAG_D5) { // 第一个字节对上，继续读第二个字节
				if ((byte) frameData[1] == FRAME_HEAD_FLAG_C8) { // 第二个字节也对上，读取后面的帧
					FramePacket framePacket = new FramePacket(frameData,len);
					framePacket.setIpAddress(framePackage.getAddress().getHostAddress().toString());
					framePacket.setPort(framePackage.getPort());
					//处理这帧数据
					processClientFrame(framePacket);
				}
			}

		}

		/**
		 * 
		 * @param framePacket
		 */
		private void processClientFrame(FramePacket framePacket) {
			short frameType;
			frameType = (short) framePacket.getFrameType();
			Log.i(TAG, "frameType--->" + frameType);
			switch (frameType) {

			case FrameType.LAN_CLIENT_REGISTER_REQUEST: // 客户端注册请求
				clientRegisterRequest(framePacket);
				break;
			case FrameType.LAN_CLIENT_UNREGISTER_REQUEST: // 客户端注销请求
				clientUnregisterRequest(framePacket);
				break;
			case FrameType.LAN_CLIENT_DATA_TYPE_REQUEST: // 查询自己注册的类型
				clientQueryDataTypeRequest(framePacket);
				break;
			case FrameType.LAN_PERSONAL_INFORMATION_REQUEST: // 客户端查询个人信息
				clientQueryUserInfo(framePacket);
				break;
//			case FrameType.LAN_FRIEND_INFORMATION_REQUEST: // 客户端查询友邻信息
//				// clientQueryFriendInfo(framePacket);
//				// break;
//
//				// TODO 测试数据发送
//				byte[] frameByte = PackageFactory.packTextMessage("123456", 7,6);
//				netService.GetExternalNet().send(1, frameByte, frameByte.length);
//				break;

			case FrameType.LAN_FRIEND_LIST_REQUEST: // 客户端查询友邻列表
				clientQueryFriendListInfo(framePacket);
				break;
			// case 84: // 客户端类型更改请求
			// clientChangeDataTypeRequest(framePacket);
			// break;
//			case FrameType.LAN_POSITION_REQUEST: // 客户端查询自身位置信息
//				break;
			case FrameType.LAN_ENVIROMENT_REQUEST: // 客户端查询自身环境信息
				break;
			default:
				/*
				 * 在这帧数据中设置本地ID，并且发送出去
				 */
				LocalProperty localProperty = NetConfig.getInstance()
						.getLocalProperty();
				framePacket.setFrameSourceID(localProperty.getDeveiceId());
				netService.GetExternalNet().send(framePacket.getDestineID(),
						framePacket.getFramePacket(),
						framePacket.getFrameLength());
				break;
			}
		}

		/**
		 * 
		 * 客户端注册请求 接收请求，并且将此socket链接与对应的客户端类型记录下来 最后将注册结果与ID返回给客户端
		 * 
		 * @param framePacket
		 */
		private void clientRegisterRequest(FramePacket framePacket) {

			byte[] clientType = new byte[1];

			/*
			 * 客户端有注册类型，就将注册类型保存到ClientTypeRecord中
			 */
			if (framePacket.getDataLength() == 1) {// 1
				clientType = framePacket.getData();
				ClientTypeRecord clientTypeRecoder = NetConfig
						.getInstance().getClientTypeRecord();
				clientTypeRecoder.record(clientType[0], framePacket.getIpAddress(),framePacket.getPort());
			}

			/*
			 * 检查是否已经在网，如果不在网络，就连接到网络（公网或者专网）
			 */
			LocalProperty localProperty = NetConfig.getInstance().getLocalProperty();
//			// 服务器处于离线状态需重新登录
//			if (localProperty.getNetType() == NetType.OFFLINE) {
//				netService.GetExternalNet().login(netService.getApplicationContext());
//			}
			// 返回客户端，告知注册结果，并将自己的ID返回给客户端
			ClientRegisterResponse(framePacket,true, localProperty.getDeveiceId());

			//一秒后启动登录
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					netService.GetExternalNet().login(netService.getApplicationContext());
				}
			}, 1000);;
			
		
		}

		/**
		 * 客户端注册请求响应函数 将注册结果成功与否以及ID返回给注册客户端
		 * 
		 * @param response
		 * @param userID
		 */
		private void ClientRegisterResponse(FramePacket framePacketOut, boolean response, int userID) {
			/*
			 * 
			 */
			//FramePacket framePacketOut = new FramePacket();
			final int dataLen = 3;
			byte[] data = new byte[dataLen]; // 存储数据内容

			/*
			 * 构造数据内容
			 */
			if (response) {
				data[0] = 1; // 注册成功响应
			} else {
				data[0] = 0; // 注册失败响应
			}

			data[2] = (byte) ((short) userID & 0xFF); // 数组靠前位置放低字节
			data[1] = (byte) (((short) userID >>> 8) & 0xFF); // 靠后位置放高字节

			/*
			 * 设置数据帧元素
			 */
			framePacketOut.setFrameSourceID((short) userID);
			framePacketOut.setFrameDestineID((short) userID);
			framePacketOut.setFrameType(FrameType.LAN_CLIENT_REGISTER_RESPONSE);
			framePacketOut.setFrameSerialNo((byte) 0);
			framePacketOut.setFrameDataLength((short) dataLen);
			framePacketOut.setFrameData(data, dataLen);

			framePacketOut.packageItemsToFrame(); // 生成数据帧

			/*
			 * 发送响应
			 */
			sendResponseToClient(framePacketOut);
		}

		/**
		 * 客户端注销请求
		 * 
		 * @param framePacket
		 */
		private void clientUnregisterRequest(FramePacket framePacket) {
			ClientTypeRecord clientTypeRecord = NetConfig.getInstance()
					.getClientTypeRecord();
			if (clientTypeRecord != null) {
				int clientId=IPv4Util.ipToInt(framePacket.getIpAddress());
				// 删除此Socket链接
				clientTypeRecord.delete(clientId);
				// if(clientSocket!=null && clientSocket.getInetAddress()!=null
				// ){
				// int
				// id=IPv4Util.ipToInt(clientSocket.getInetAddress().getHostAddress());
				// clientTypeRecord.delete(id);
				// }
				// 是否是最后一个链接，如果是则退出网络
				if (clientTypeRecord.getNumberOfClients() == 0) {
					netService.GetExternalNet().logout(netService.getApplicationContext());
				}

				clientUnregisterResponse(framePacket,true); // 返回客户端，告知注销结果

			}

		}

		/**
		 * 客户端注销响应
		 * 
		 * @param response
		 */
		private void clientUnregisterResponse(FramePacket framePacketOut,boolean response) {

//			FramePacket framePacketOut = new FramePacket();
			byte[] data = new byte[1];

			/*
			 * 构造数据内容，返回结果
			 */
			if (response) {
				data[0] = (byte) 1;
			} else {
				data[0] = (byte) 0;
			}

			/*
			 * 设置数据帧元素
			 */
			framePacketOut.setFrameSourceID((short) 0);
			framePacketOut.setFrameDestineID((short) 0);
			framePacketOut
					.setFrameType(FrameType.LAN_CLIENT_UNREGISTER_RESPONSE);
			framePacketOut.setFrameSerialNo((byte) 1);
			framePacketOut.setFrameDataLength((short) 1);
			framePacketOut.setFrameData(data, 1);

			framePacketOut.packageItemsToFrame(); // 生成数据帧

			/*
			 * 发送响应
			 */
			sendResponseToClient(framePacketOut);
		}

		/**
		 * 客户端更改数据类型请求
		 * 
		 * @param framePacket
		 */
		private void clientChangeDataTypeRequest(FramePacket framePacket) {

			byte[] clientType = new byte[1];

			/*
			 * 客户端有注册类型，就将注册类型保存到中
			 */
			if (framePacket.getDataLength() == 1) {
				clientType = framePacket.getData();
				ClientTypeRecord clientTypeRecord = NetConfig
						.getInstance().getClientTypeRecord();
				clientTypeRecord.record(clientType[0], framePacket.getIpAddress(),framePacket.getPort());

			}

			/*
			 * 返回给客户端变更请求结果
			 */
			clientChangeDataTypeResponse(true);

		}

		/**
		 * 更改数据类型通知（广播）
		 */
		/*
		 * 应该放在ExternalTransThread里 private void ChangeDataTypeBroadcast(){
		 * 
		 * }
		 */
		/**
		 * 客户端更改数据类型请求的响应
		 * 
		 * @param response
		 */
		private void clientChangeDataTypeResponse(boolean response) {

			FramePacket framePacketOut = new FramePacket();
			final int dataLen = 1;
			byte[] data = new byte[dataLen]; // 存储数据内容

			// 构造数据内容
			if (response) {
				data[0] = 1; // 注册成功响应
			} else {
				data[0] = 0; // 注册失败响应
			}

			/*
			 * 设置数据帧元素
			 */
			framePacketOut.setFrameSourceID((short) 0);
			framePacketOut.setFrameDestineID((short) 0);
			framePacketOut
					.setFrameType(FrameType.LAN_CLIENT_CHANGE_DATA_TYPE_RESPONSE);
			framePacketOut.setFrameSerialNo((byte) 1);
			framePacketOut.setFrameDataLength((short) dataLen);
			framePacketOut.setFrameData(data, dataLen);
			framePacketOut.packageItemsToFrame(); // 生成数据帧

			/*
			 * 发送响应
			 */
			sendResponseToClient(framePacketOut);

		}

		/**
		 * 
		 * @param framePacket
		 */
		private void clientQueryDataTypeRequest(FramePacket framePacket) {
			byte registeredDataType;
			ClientTypeRecord clientTypeRecord = NetConfig.getInstance()
					.getClientTypeRecord();
			int clientId=IPv4Util.ipToInt(framePacket.getIpAddress());
			registeredDataType = clientTypeRecord.getRegisteredType(clientId);
			clientQueryDataTypeResponse(framePacket,true, registeredDataType);
		}

		/**
		 * 
		 * @param response
		 * @param resgisterDataType
		 */
		private void clientQueryDataTypeResponse(FramePacket framePacketOut,boolean response,
				byte resgisterDataType) {
			/*
			 * 
			 */
//			FramePacket framePacketOut = new FramePacket();
			final int dataLen = 2;
			byte[] data = new byte[dataLen]; // 存储数据内容

			/*
			 * 构造数据内容
			 */
			if (response) {
				data[0] = 1; // 注册成功响应
			} else {
				data[0] = 0; // 注册失败响应
			}

			/*
			 * 数据内容
			 */
			data[1] = resgisterDataType;

			/*
			 * 设置数据帧元素
			 */
			framePacketOut.setFrameSourceID((short) 0);
			framePacketOut.setFrameDestineID((short) 0);
			framePacketOut
					.setFrameType(FrameType.LAN_SUPPORT_DATA_TYPE_RESPONSE);
			framePacketOut.setFrameSerialNo((byte) 1);
			framePacketOut.setFrameDataLength((short) dataLen);
			framePacketOut.setFrameData(data, dataLen);

			framePacketOut.packageItemsToFrame(); // 生成数据帧

			/*
			 * 发送响应
			 */
			sendResponseToClient(framePacketOut);
		}

		// 客户端查询个人资料
		public void clientQueryUserInfo(FramePacket framePackage) {
			NetConfig app = NetConfig.getInstance();
			LocalProperty local = app.getLocalProperty();
			if (local.getNetType() == NetType.INTERNET) {
				framePackage.setFrameDestineID(0);
				netService.GetExternalNet().sendFrame(framePackage);
			} else {
				FramePacket framePacketOut = new FramePacket();
				framePacketOut.setIpAddress(framePackage.getIpAddress());
				framePacketOut.setPort(framePackage.getPort());
				// 构造本地服务器自身的数据（友邻资料）
				// id username ip 注册类型、网络状态
				String ipAddress = app.getLocalProperty().getIpAddress();
				String userName = app.getLocalProperty().getName();
				int ip = IPv4Util.ipToInt(ipAddress);
				int clientId = app.getLocalProperty().getDeveiceId();
				List<Client> clients = app.getClientTypeRecord().getClients();
				int type = 0;
				if (clients != null && clients.size() > 0) {
					type = clients.get(0).getClientType();
					for (int i = 0; i < clients.size(); i++) {
						type = (int) type
								| (int) clients.get(i).getClientType();
					}

				}
				byte registType = (byte) type;

				NetType state = app.getLocalProperty().getNetType();
				byte netState = 0;
				if (state == NetType.INTERNET) {
					netState = 1;
				} else if (state == NetType.WSN) {
					netState = 2;
				} else {
					netState = 0;

				}
				// 网络类型暂时不传入 枚举转为字节
				byte[] friendData = new byte[38];

				byte[] name = userName.getBytes();
				byte[] ipByte = TypeConvert.int2byte(ip);
				int length = name.length > 32 ? 32 : name.length;
				System.arraycopy(name, 0, friendData, 0, length); // 用户名称
				System.arraycopy(ipByte, 0, friendData, 32, 4); // ip地址
				friendData[36] = registType; // 注册类型
				friendData[37] = netState; // 网络状态
				framePacketOut.setFrameSourceID((short) clientId);
				framePacketOut.setFrameDestineID((short) 0);
				framePacketOut.setFrameType(FrameType.LAN_CLIENT_UNREGISTER_RESPONSE);
				framePacketOut.setFrameSerialNo((byte) 1);
				framePacketOut.setFrameDataLength((short) 1);
				framePacketOut.setFrameData(friendData, 38);
				framePacketOut.packageItemsToFrame(); // 生成数据帧
				/*
				 * 发送响应
				 */
				sendResponseToClient(framePacketOut);
			}

		}

		// 客户端查询友邻信息资料
		public void clientQueryFriendInfo(FramePacket framePackage) {
			LocalProperty local = NetConfig.getInstance()
					.getLocalProperty();
			int sourceId = local.getDeveiceId();
			// if(local.getNetType()==NetType.INTERNET){
			// framePackage.setFrameType(47); //获取在线设备ID
			//
			// }
			framePackage.setFrameSourceID(sourceId);
			framePackage.packageItemsToFrame();
			netService.GetExternalNet().sendFrame(framePackage);

		}

		// 客户端查询友邻列表信息
		public void clientQueryFriendListInfo(FramePacket framePacket) {
			LocalProperty local = NetConfig.getInstance()
					.getLocalProperty();
			framePacket.setFrameSourceID(local.getDeveiceId());
			
			
			
			if(local.getNetType()==NetType.INTERNET){
			 framePacket.setFrameType(FrameType.INTENET_ONLINE_DEVICE_REQUEST); //获取在线设备ID
			 framePacket.setFrameDestineID(1);
			
			 }else if(local.getNetType()==NetType.WSN){
				 framePacket.setFrameType(FrameType.LAN_FRIEND_INFORMATION_REQUEST); // 获取友邻列表
				 framePacket.setFrameDestineID(0);
			 }
			
			framePacket.packageItemsToFrame();

			netService.GetExternalNet().sendFrame(framePacket);
		}

		/**
		 * 发送响应到客户端
		 * 
		 * @param framePacket
		 */
		private void sendResponseToClient(FramePacket framePacket) {
			sendPacket.setData(framePacket.getFramePacket());
			sendPacket.setLength(framePacket.getFramePacket().length);
			try {
				sendPacket.setAddress(InetAddress.getByName(framePacket.getIpAddress()));
				sendPacket.setPort(framePacket.getPort());
				sendSocket.send(sendPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception e) {
				// TODO: handle exception
			}
		}

	}

}
