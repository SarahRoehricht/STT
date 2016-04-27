package demo.sphinx.helloworld;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ReceiveUDP
{
	private InetAddress myReceiverAdress;
	private int myReceicerPort;

	/**
	 * 
	 * @param myReceiverAdress
	 *            the adress of the interface to receive data from (my IP-Adress)
	 * @param myReceicerPort
	 *            the port to listen to
	 */
	public ReceiveUDP(InetAddress myReceiverAdress, int myReceicerPort)
	{
		super();
		this.myReceiverAdress = myReceiverAdress;
		this.myReceicerPort = myReceicerPort;
	}

	/**
	 * 
	 * @param block
	 * @param senderAdress
	 * @param senderPort
	 * @return
	 * @throws Exception
	 */
	public String receiveChannel(boolean block, InetAddress senderAdress, int senderPort)
	{
		String result = null;
		try
		{
			DatagramChannel receivingChannel = DatagramChannel.open();

			SocketAddress receivingSocket = new InetSocketAddress(myReceiverAdress, myReceicerPort);
			InetSocketAddress sendingSocket = new InetSocketAddress(senderAdress, senderPort);

			receivingChannel.socket().bind(receivingSocket);
			receivingChannel.configureBlocking(block);
			receivingChannel.connect(sendingSocket);

			ByteBuffer packet = ByteBuffer.allocate(1024);
			packet.clear();
			// System.out.println(((InetSocketAddress) receivingSocket).getAddress() + ":" + ((InetSocketAddress) receivingSocket).getPort());

			if (receivingChannel.receive(packet) != null)
				result = new String(packet.array());

			receivingChannel.close();
		} catch (SocketException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public String receiveSocket()
	{
		DatagramPacket request = null;
		try
		{
			DatagramSocket socket = new DatagramSocket(myReceicerPort, myReceiverAdress);

			byte[] receiveData = new byte[1024];
			request = new DatagramPacket(receiveData, receiveData.length);

			socket.receive(request);

			socket.close();
		} catch (UnknownHostException e)
		{
			System.err.println("Host Adress not found!");
			e.printStackTrace();
		} catch (SocketException e)
		{
			System.err.println("Socket creation error!");
			e.printStackTrace();
		} catch (IOException e)
		{
			System.err.println("Error on receiving package!");
			e.printStackTrace();
		}

		return new String(request.getData(), request.getOffset(), request.getLength());
	}

}