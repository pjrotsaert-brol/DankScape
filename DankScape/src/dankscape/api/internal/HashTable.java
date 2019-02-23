/*
 * DankScape - An Old-School Runescape Bot writter by Pieter-Jan Rotsaert
 * Please do not (re-)distribute or use without consent.
 */
package dankscape.api.internal;

import dankscape.api.rs.RSHashTable;
import dankscape.api.rs.RSItemComposition;
import dankscape.api.rs.RSNode;
import dankscape.loader.AppletLoader;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Pieterjan
 */
public class HashTable 
{
	private final RSHashTable peer;
	private RSNode current;
	private int c_index = 0;
        
        private static final HashMap<RSHashTable, HashTable> cache = new HashMap();

	private HashTable(RSHashTable peer) 
        {
		this.peer = peer;
	}
        
        public static HashTable fromPeer(RSHashTable peer)
        {
            HashTable tbl = cache.get(peer);
            if(tbl == null)
            {
                tbl = new HashTable(peer);
                cache.put(peer, tbl);
            }
            return tbl;
        }

	public RSNode getFirst() 
        {
		c_index = 0;
		return getNext();
	}

	public RSNode getNext() 
        {
                RSNode[] buckets = peer.getBuckets();
                
		if (c_index > 0 && buckets[c_index - 1] != current) 
                {
			RSNode node = current;
			current = node.getPrevious();
			return node;
		}
		while (c_index < buckets.length) 
                {
			RSNode node = buckets[c_index++].getPrevious();
			if (buckets[c_index - 1] != node) 
                        {
				current = node.getPrevious();
				return node;
			}
		}
		return null;
	}
        
        public RSNode find(int id)
        {
            System.out.println("Attempting to find item " + id + "...");
            int nChecked = 0, kek=0;
            RSNode[] buckets = peer.getBuckets();
            for(RSNode bucket : buckets)
            {
                System.out.println("Checking bucket " + kek++ + ".");
                for(RSNode node = bucket.getNext();node != null && node != bucket;node = node.getNext())
                {
                    nChecked++;
                    if((int)node.getHash() == id)
                        return node;
                }
            }
            System.out.println("Item not found, checked " + nChecked + " times.");
            return null;
            //return find((long)id);
            /*int idx = 0;
            RSNode first = getFirst();
            for(RSNode n = first;n != null;n = getNext())
            {
                if(n == first && idx > 0)
                    break;
                if(n.getHash() == (long)id)
                    return n;
                idx++;
            }
            System.out.println("Searched " + idx + " nodes. total size =>" + peer.getSize());
            return null;*/
            //return find((long)id);
        }
        
        public RSNode find(long id) 
        {
            try 
            {
                if ((peer == null) || (peer.getBuckets() == null) || (id < 0)) 
                {
                    return null;
                }

                System.out.println("Num buckets: " + peer.getBuckets().length);
                int nSearched = 0;
                HashSet<Integer> nUnique = new HashSet();
                for(int curBucket = 0;curBucket < peer.getBuckets().length;curBucket++)
                {
                    if(peer.getBuckets()[curBucket] == null)
                        continue;
                    
                    final RSNode n = peer.getBuckets()[(int) (curBucket)];

                    //n.dumpDebug();

                    System.out.println ("NODE: Previous: " + n.getPrevious().getClass().getName() + "(" + n.getPrevious().getHash() + 
                            ")  Next: " + n.getNext().getClass().getName() + " (" + n.getNext().getHash() + ")");

                    

                    int firstHash = 0;
                    
                    for (RSNode node = n.getPrevious();node != n && node != null && nSearched < 100000;node = node.getPrevious()) 
                    {


                            if (node.getHash() == id) 
                                    return node;

                        RSNode nodeNode = (RSNode)RSClassWrapper.getWrapper(AppletLoader.getSingleton().getFieldValue("Node", "previous", node.getRSObjectReference()));


                        /*System.out.println(node.getClass().getName() + ": " + node.getHash() + ":  nodeNext => " +
                                (nodeNode == null ? "null" : "" + nodeNode.getHash()) + (nodeNode != null ? "(" + nodeNode.getClass().getName() + ")" : "")
                                + "  cacheNext => " + node.getPrevious().getHash() + " (" + node.getPrevious().getClass().getName() + ")");*/

                        nUnique.add((int)node.getHash());
                        if(nSearched == 0)
                            firstHash = (int)node.getHash();
                        else if((int)node.getHash() == firstHash)
                        {
                            System.out.println("Shit is duplicate...");
                            break;
                        }

                        nSearched++;
                    }
                }
                System.out.println("Searched " + nSearched + " nodes, " + nUnique.size() + " unique values.");

            } 

            catch (final Exception ignored) {}
            return null;
	}
}