package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import bp.config.BPConfig;
import bp.config.UIConfigs;
import bp.data.BPTextContainer;
import bp.data.BPTextContainerBase;
import bp.format.BPFormat;
import bp.format.BPFormatJSON;
import bp.format.BPFormatText;
import bp.format.BPFormatWebSiteOperation;
import bp.res.BPResource;
import bp.res.BPResourceFileSystem;
import bp.res.BPResourceWebSiteLink;
import bp.ui.form.BPFormPanelWebSiteOpeation;
import bp.ui.scomp.BPWebSiteOperationPane;
import bp.ui.scomp.BPWebSiteOperationResultPane;
import bp.ui.scomp.BPSplitPane;
import bp.ui.scomp.BPTextPane;
import bp.ui.util.UIUtil;
import bp.util.Std;
import bp.web.BPWebOperation;
import bp.web.BPWebOperationBase;

public class BPWebSiteOperationPanel extends BPCodePanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7094035370222028475L;

	protected BPFormPanelWebSiteOpeation m_opform;
	protected AtomicBoolean m_changed;
	protected boolean m_istextconchange = false;
	protected JScrollPane m_psrc;
	protected BPWebSiteOperationResultPane m_result;
	protected BPSplitPane m_sp;
	protected BPSplitPane m_sp0;

	protected BPTextPane createTextPane()
	{
		return new BPWebSiteOperationPane();
	}

	protected void init()
	{
		m_changed = new AtomicBoolean(false);
		m_sp = new BPSplitPane(BPSplitPane.HORIZONTAL_SPLIT);
		m_sp0 = new BPSplitPane(BPSplitPane.VERTICAL_SPLIT);
		m_scroll = new JScrollPane();
		m_psrc = m_scroll;
		m_result = new BPWebSiteOperationResultPane(this);
		JPanel pansp = new JPanel();
		JPanel panright = new JPanel();

		m_txt = createTextPane();
		m_opform = new BPFormPanelWebSiteOpeation();

		m_txt.setMonoFont();

		pansp.setBorder(new MatteBorder(0, 0, 1, 0, UIConfigs.COLOR_WEAKBORDER()));
		m_psrc.setBorder(new MatteBorder(0, 0, 0, 1, UIConfigs.COLOR_WEAKBORDER()));
		m_sp0.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_sp.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_psrc.setPreferredSize(new Dimension(1600, 0));
		panright.setPreferredSize(new Dimension(200, 0));
		pansp.setPreferredSize(new Dimension(0, 600));
		m_result.setPreferredSize(new Dimension(0, 200));

		m_sp0.setDividerSize(4);
		m_sp0.setDividerBorderColor(UIConfigs.COLOR_TEXTHALF(), false);
		m_sp.setResizeWeight(0.9);
		m_sp0.setResizeWeight(0.75);

		pansp.setLayout(new BorderLayout());
		panright.setLayout(new BorderLayout());
		setLayout(new BorderLayout());
		m_psrc.setViewportView(m_txt);
		panright.add(m_opform, BorderLayout.CENTER);
		pansp.add(m_sp, BorderLayout.CENTER);
		m_sp.setLeftComponent(m_psrc);
		m_sp.setRightComponent(panright);
		m_sp.setDividerBorderColor(UIConfigs.COLOR_STRONGBORDER(), false);
		m_sp.setDividerSize(4);
		m_sp0.setLeftComponent(pansp);
		m_sp0.setRightComponent(m_result);
		add(m_sp0, BorderLayout.CENTER);
		m_sp0.setReservedSize((int) (400 * UIConfigs.UI_SCALE()) - 1);

		m_txt.setOnPosChanged(this::onPosChanged);
		m_txt.resizeDoc();

		m_opform.setCallback(this::onOperationChanged);
		updateLineProp();
	}

	protected void onOperationChanged(String key, Map<String, Object> formdata)
	{
		if ("contentText".equals(key))
		{
			String contenttext = (String) formdata.get("contentText");
			BPWebOperationBase op = getCurrentLineOP();
			if (op != null)
			{
				op.setContent(contenttext);
				setCurrentLineOP(op);
			}
		}
	}

	protected void setCurrentLineOP(BPWebOperation op)
	{
		String line = op.toLineText();
		int l = m_txt.getCaretPosition();
		String text = m_txt.getViewText();
		int lp = text.lastIndexOf("\n", l - 1);
		int np = text.indexOf("\n", l);
		if (np == -1)
			np = text.length();
		try
		{
			Document doc = m_txt.getDocument();
			doc.remove(lp + 1, np - lp - 1);
			doc.insertString(lp + 1, line, null);
		}
		catch (BadLocationException e)
		{
			Std.err(e);
		}
	}

	protected BPWebOperationBase getCurrentLineOP()
	{
		int l = m_txt.getCaretPosition();
		String text = m_txt.getViewText();
		int lp = text.lastIndexOf("\n", l - 1);
		int np = text.indexOf("\n", l);
		if (np == -1)
			np = text.length();
		if (np <= lp)
			return null;
		String line = text.substring(lp + 1, np);
		BPWebOperationBase op = BPWebOperationBase.parse(line);
		return op;
	}

	protected void onPosChanged(int row, int col)
	{
		super.onPosChanged(row, col);
		updateLineProp();
	}

	protected void updateLineProp()
	{
		m_changed.set(true);
		UIUtil.laterUI(() ->
		{
			if (m_changed.compareAndSet(true, false))
			{
				setLineProp();
			}
		});
	}

	public void setContextByWebSiteLink(BPResourceWebSiteLink bpResourceWebSiteLink)
	{
	}

	protected void setLineProp()
	{
		BPWebOperationBase op = getCurrentLineOP();
		m_opform.showData(op.getMappedData());
	}

	public final static class BPEditorFactoryWebSiteOperation implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { BPFormatWebSiteOperation.FORMAT_WEBSITEOP };
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			BPWebSiteOperationPanel rc = new BPWebSiteOperationPanel();
			if (params != null && params.length > 0)
				rc.setContextByWebSiteLink((BPResourceWebSiteLink) params[0]);
			return rc;
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
			if (res.isFileSystem() && ((BPResourceFileSystem) res).isFile())
			{
				BPTextContainer con = new BPTextContainerBase();
				con.bind(res);
				((BPWebSiteOperationPanel) editor).bind(con, ((BPResourceFileSystem) res).getTempID() != null);
			}
		}

		public String getName()
		{
			return "WebSite Console";
		}
	}

	public List<BPWebOperation> getWebOperations()
	{
		String text = m_txt.getViewText();
		String[] lines = text.split("\n");
		List<BPWebOperation> rc = new ArrayList<BPWebOperation>();
		for (String line : lines)
		{
			if (line != null && line.trim().length() > 0)
			{
				BPWebOperation op = BPWebOperationBase.parse(line);
				if (op != null)
				{
					rc.add(op);
				}
			}
		}
		return rc;
	}

	@SuppressWarnings("unchecked")
	public <T> T getViewerData(String part, String format)
	{
		if (part == null || "result".equals(part))
			return (T) m_result.getResult(format);
		return null;
	}

	public String[] getViewerFormat()
	{
		return new String[] { BPFormatText.FORMAT_TEXT, BPFormatJSON.FORMAT_JSON };
	}
}
