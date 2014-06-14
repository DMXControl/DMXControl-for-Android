// SuperCollider Tests for NetUtil v0.3
// lastmod: 14-Oct-06 sciss

// --testUDPServer
(
fork {
	n = NetAddr( "localhost", 0x5454 );
	r = OSCresponderNode( n, '/done', { arg time, resp, msg;
		("reply : " ++ msg.asString).postln;
	}).add;
	n.sendMsg( '/dumpOSC', 3 );
	n.sendMsg( '/pause' );
	6.wait;
	n.sendMsg( '/quit' );
	r.remove;
}
)
// --testTCPServer

(
fork {
	n = NetAddr( "localhost", 0x5454 );
	r = OSCresponderNode( n, '/done', { arg time, resp, msg;
		("reply : " ++ msg.asString).postln;
	}).add;
	("isConnected ? " ++ n.isConnected).inform;	// --> false
	n.connect;
	("isConnected ? " ++ n.isConnected).inform;	// --> true hopefully
	n.sendMsg( '/dumpOSC', 3 );
	n.sendMsg( '/pause' );
	2.wait;
	("isConnected ? " ++ n.isConnected).inform;	// --> false
	3.wait;
	n.connect;
	("isConnected ? " ++ n.isConnected).inform;	// --> true hopefully
	n.sendMsg( '/quit' );
	r.remove;
}
)