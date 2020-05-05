package jhc.image.reader;

import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.awt.image.Raster;

/**
 * Created by IntelliJ IDEA.
 * User: sigolaev_va
 * Date: 31.10.2013
 * Time: 9:32:53
 * To change this template use File | Settings | File Templates.
 */
public interface IRasterReader
{
	int getRGB(int x, int y);
	void putRGB(int x, int y, int rgb);

	public static class RasterReader implements IRasterReader
	{
		private int m_type;
		private DataBuffer m_buffer;
		Object m_obj;
		private Raster m_raster;
		private int m_width;
		private int m_height;

		public RasterReader( Raster raster)
		{
			m_width = raster.getWidth();
			m_height = raster.getHeight();
			m_type = (m_buffer = (m_raster = raster).getDataBuffer()).getDataType();
			switch ( m_type )
			{
				case DataBuffer.TYPE_BYTE:
					m_obj = new byte[1];
					break;
				case DataBuffer.TYPE_INT:
					m_obj = new int[1];
					break;
				case DataBuffer.TYPE_SHORT:
				case DataBuffer.TYPE_USHORT:
					m_obj = new short[1];
					break;
				case DataBuffer.TYPE_FLOAT:
					m_obj = new float[1];
					break;
				case DataBuffer.TYPE_DOUBLE:
					m_obj = new double[1];
					break;
				case DataBuffer.TYPE_UNDEFINED:
					throw new IllegalArgumentException( "data type TYPE_UNDEFINED is no supported yet" );
			}
		}

		/**
		 * Gets full RGB data, copy same value to R G and B channels if needed
		 * @param x
		 * @param y
		 * @return
		 */
		public int getRGB( int x, int y )
		{
			m_obj = m_raster.getDataElements( x, y, m_obj );
			switch ( m_type )
			{
				case DataBuffer.TYPE_BYTE:
					byte bt = ((byte[])m_obj)[0];
					return (bt & 0x000000FF) + ((bt << 8)& 0x0000FF00) + ((bt << 16)& 0x00FF0000);
				case DataBuffer.TYPE_INT:
					return ((int[])m_obj)[0];
				case DataBuffer.TYPE_SHORT:
				case DataBuffer.TYPE_USHORT:
					return (((short[])m_obj)[0]&0x0000FFFF);
				case DataBuffer.TYPE_FLOAT:
					return (int)((float[])m_obj)[0];
				case DataBuffer.TYPE_DOUBLE:
					return (int)((double[])m_obj)[0];
				default:
					throw new IllegalArgumentException( "data type TYPE_UNDEFINED is no supported yet" );
			}
		}

		public void putRGB( int x, int y, int rgb )
		{
/*
			switch ( m_type )
			{
				case DataBuffer.TYPE_BYTE:
					((byte[])m_obj)[0] = (byte)rgb;
					break;
				case DataBuffer.TYPE_INT:
					((int[])m_obj)[0] = rgb;
					break;
				case DataBuffer.TYPE_SHORT:
				case DataBuffer.TYPE_USHORT:
					((short[])m_obj)[0] = (short)rgb;
					break;
				case DataBuffer.TYPE_FLOAT:
					((float[])m_obj)[0] = rgb;
					break;
				case DataBuffer.TYPE_DOUBLE:
					((double[])m_obj)[0] = rgb;
					break;
				default:
					throw new IllegalArgumentException( "data type TYPE_UNDEFINED is no supported yet" );
			}
			m_raster.setDataElements(  x, y, m_obj );
*/
		}
	}

}

