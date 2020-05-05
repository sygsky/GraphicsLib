package ar.com.hjg.pngj;

/**
 * Bytes consumer. Objects implementing this interface can act as bytes
 * consumers, that are "fed" with bytes.
 */
public interface IBytesConsumer {
	/**
	 * 
	 * 
	 * Returns bytes actually consumed, -1 if we are done Should return some
	 * value between 1 and len should never return 0 (unless len=0)
	 * 
	 */
	int consume(byte[] buf, int offset, int len);
}
