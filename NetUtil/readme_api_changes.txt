In the course of the development of NetUtil, some API changes were made. Generally, changing the API is considered bad because it breaks previous code (it is not backward compatible). However, I tried to minimize these changes. Generally, changes have only been made, when the API proved suboptimal or new features were added which would introduce ugly API when enforcing backward compatibility.

This document tries to help updating code that uses old versions of NetUtil.

API changes from v0.22 to v0.24
	- in interface de.sciss.net.OSCListener
		old signature:
			public void messageReceived( OSCMessage m, SocketAddress addr );
		new signature:
			public void messageReceived( OSCMessage m, SocketAddress addr, long when );

		this change was necessary to pass bundle execution times to the listener. to update
		old code, simply replace the old signatures with the new ones and ignore the
		bundle time (long when).

API changes from v0.26 to v0.30
- these changes were made in the course of TCP integration
	- deletion of class de.sciss.net.AbstractOSCCommunicator
		why? the class was too specific about the network channel type. the class name
		is ugly. the class was used to define constants, which is generally done
		by using an interface.

		therefore, the interface de.sciss.net.OSCChannel was created which includes
		some of the methods of AbstractOSCCommunicator.

		in your code, try to replace references to AbstractOSCCommunicator by their
		concrete subclass OSCReceiver or OSCTransmitter. Getting the DatagramChannel
		is impossible now. Instead memorize the channel you pass to one of the
		constructors in OSCReceiver or OSCTransmitter. If you used the empty
		constructor, create an explicit channel that can be memorized.

		if you just need to learn the local socket address, you can call
		getLocalAddress() now.

	- de.sciss.net.OSCReceiver and de.sciss.net.OSCTransmitter became abstract
		making these classes abstract provides a much more beautiful way of dealing
		with different transport types than using internal delegates.

		this implies that you cannot construct instances directly. instead new
		static methods newUsing() are provided. examples:

			old code:	rcv = new OSCReceiver();
			new code:	rcv = OSCReceiver.newUsing( OSCReceiver.UDP );

			old code:	rcv = new OSCReceiver( myChannel, myAddr, myBufSize );
			new code:	myChannel.socket().bind( myLocalAddr );
					rcv = OSCReceiver.newUsing( myChannel );
					rcv.setBufferSize( myBufSize );
					// note: filtering for myAddr is not possible any more
					// ; this will be addressed in OSCServer in a next version

		however, things have been further simplified with the introduction of classes
		de.sciss.net.OSCClient and de.sciss.net.OSCServer, so you may wish to exchange
		a combo instantiation of OSCReceiver and OSCTransmitter by one of these classes.

API changes from v0.32 to v0.33
- these changes were made in the course of codec customization
	- removals in OSCBundle
		decodeBundle( ByteBuffer b, Map m ) removed

	- removals in OSCPacket
		protected codec helping field 'pad' and methods 'readString', 'terminateAndPadToAlign',
		'padToAlign', 'skipToValues', 'skipToAlign' removed or moved to OSCPacketCodec.

	- changes in OSCPacket
		getSize is final now, not abstract any more.
		encode is final now, not abstract any more.
		encode doesn't throw BufferOverflowException any more.
		decode doesn't throw BufferUnderflowException any more.
		
		old signature:
			public static OSCPacket decode( ByteBuffer b, Map m )
		new signature:
			public static OSCPacket decode( ByteBuffer b )

		(due to removal of SpecificOSCMessage)

	- removals in OSCReceiver
		setCustomMessageDecoders() was removed (due to removal of SpecificOSCMessage).

	- changes in OSCTransmitter
		send( OSCPacket p, SocketAddress target ) is final now, not abstract any more

	- removal of SpecificOSCMessage
		this class war very ugly and unflexible. to use specifically optimized message
		encoding / decoding, you should instead subclass OSCPacketCodec and overwrite
		the corresponding encode() and decode() methods. A possible conversion approach
		you can find in SwingOSC v0.53+'s ScopeView and OSCSharedBufSetNMsg classes.

API changes from v0.36 to v0.35
- OSCReceiver binds to IP "0.0.0.0" instead of InetAddress.getLocalHost() when loopBack is false.
  This allows sockets bound to loopback to send to those receivers, too.
	- changes in OSCChannel
		getLocalAddress now throws an IOException. In order to prevent problems with
		returning "0.0.0.0", in such case InetAddress.getLocalHost() is returned which is
		most likely the desired result, besides staying mostly backwards compatible.
		getLocalHost() may throw however the IOException (UnknownHostException).

---


lastmod: 06-may-09 sciss
