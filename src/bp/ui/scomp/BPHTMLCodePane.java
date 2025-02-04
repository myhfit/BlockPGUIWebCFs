package bp.ui.scomp;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import bp.util.ClipboardUtil;

public class BPHTMLCodePane extends BPCodePane
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4823205250761353119L;

	public void paste()
	{
		Clipboard cl = getToolkit().getSystemClipboard();
		Transferable tf = cl.getContents(null);
		DataFlavor[] dfs = tf.getTransferDataFlavors();
		boolean[] dfcls = ClipboardUtil.checkClassFromDataFlavers(dfs);
		if (dfcls[1])
		{
			try
			{
				BufferedImage img = (BufferedImage) tf.getTransferData(DataFlavor.imageFlavor);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				String format = "png";
				ImageIO.write(img, format, bos);
				byte[] bs = Base64.getEncoder().encode(bos.toByteArray());
				String text = "<img src=\"data:image/" + format + ";base64," + new String(bs)+"\"/>";
				insertOrReplace(text);
			}
			catch (UnsupportedFlavorException | IOException e)
			{
				e.printStackTrace();
			}
		}
		else
			super.paste();
	}
}
