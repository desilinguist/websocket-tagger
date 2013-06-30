import java.io.StringReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.pipeline.DefaultPaths;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;


/**
 * A simple WebSocket server that returns POS tags
 */
public class StanfordTaggerServer extends WebSocketServer {

    public static MaxentTagger tagger;

    public StanfordTaggerServer( int port ) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
    }

    public StanfordTaggerServer( InetSocketAddress address ) {
        super( address );
    }

    // This is just a stub since we are not doing anything special
    // when the connection is opened
    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) { }


    // Another stub for when the connection is closed
    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
    }

    // The method called when a message is sent from the client
    // to the server
    @Override
    public void onMessage( WebSocket conn, String message ) {

        // assume no tokenization was done; use Stanford's default tokenizer
        DocumentPreprocessor preprocess = new DocumentPreprocessor(new StringReader(message));
        Iterator<List<HasWord>> foundSentences = preprocess.iterator();

        // Part of speech tag each tokenized sentence and join with newlines
        StringBuilder sb = new StringBuilder();
        while (foundSentences.hasNext())
        {
            sb.append(this.tagSentence(foundSentences.next()));
            sb.append("\n");
        }

        // send the tagged text back to the client
        this.reply(sb.toString());

    }

    // The main class
    public static void main( String[] args ) throws InterruptedException , IOException {

        // Default port
        int port = 9090; // 843 flash policy port

        // Were we given a port instead?
        try {
            port = Integer.parseInt( args[0] );
        }
        catch ( Exception ex ) {

        }

        // Initialize the server
        StanfordTaggerServer s = new StanfordTaggerServer( port );

        // Initialize the tagger model
        StanfordTaggerServer.tagger = new MaxentTagger(DefaultPaths.DEFAULT_POS_MODEL);

        // Start the server
        s.start();
        System.out.println( "StanfordTaggerServer started on port: " + s.getPort() );

    }

    // What happens if we have an error
    @Override
    public void onError( WebSocket conn, Exception ex ) {
        ex.printStackTrace();
        if( conn != null ) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    // private method to tag the given sentence and return a tagged sentence string
    private String tagSentence(List<HasWord> sentence) {
        List<String> taggedSentence = new ArrayList<String>();

        List<TaggedWord> outputFromTagger = this.tagger.apply(sentence);
        for (TaggedWord tw : outputFromTagger)
        {
            String tws = tw.toString();
            taggedSentence.add(tws);
        }
        StringBuilder sb = new StringBuilder();
        for (String s : taggedSentence)
        {
            sb.append(s);
            sb.append(" ");
        }
        return sb.toString();
    }

    // private method to send a reply back to the string
    private void reply(String text) {

        // iterative over all open connections
        Collection<WebSocket> con = connections();
        synchronized ( con ) {
            for( WebSocket c : con ) {
                c.send(text);
            }
        }
    }

}
