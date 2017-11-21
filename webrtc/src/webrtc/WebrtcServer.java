package webrtc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

public class WebrtcServer {

	private static List<SocketIOClient> clients = new ArrayList<SocketIOClient>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Configuration config = new Configuration();
		//设置主机名
		config.setHostname("10.0.0.16");
		//设置监听端口
		config.setPort(6666);
		final SocketIOServer server = new SocketIOServer(config);
		//加入连接监听事件
		server.addConnectListener(new ConnectListener() {

			@Override
			public void onConnect(SocketIOClient client) {
				if(clients.size()!=0){
					client.sendEvent("SomeOneOnline", "");
				}
				System.out.println(client.getSessionId().toString()+"已连接");
				clients.add(client);
			}
		});
		//断开连接监听事件
		server.addDisconnectListener(new DisconnectListener() {

			@Override
			public void onDisconnect(SocketIOClient client) {
				System.err.println(client.getSessionId().toString()+"已断开");
				for (SocketIOClient c : clients) {
					if(client.getSessionId() == c.getSessionId()){
						clients.remove(c);
						break;
					}
				}
			}
		});

		server.addEventListener("SdpInfo", String.class,new DataListener<String>() {

			@Override
			public void onData(SocketIOClient client, String data,
					AckRequest ackSender) throws Exception {
				getOtherClient(client).sendEvent("SdpInfo",data);
			}
		});
		
		server.addEventListener("IceInfo", String.class,new DataListener<String>() {

			@Override
			public void onData(SocketIOClient client, String data,
					AckRequest ackSender) throws Exception {
				getOtherClient(client).sendEvent("IceInfo",data);
			}
		});
		
		//启动服务
		server.start();

	}
	
	private static SocketIOClient getOtherClient(SocketIOClient client){
		SocketIOClient c = null;
		for (SocketIOClient socketIOClient : clients) {
			if(socketIOClient != client){
				c = socketIOClient;
				break;
			}
		}
		return c;
	}

}
