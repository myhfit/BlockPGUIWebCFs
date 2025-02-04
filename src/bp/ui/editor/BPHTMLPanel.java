package bp.ui.editor;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import bp.config.BPConfig;
import bp.config.UIConfigs;
import bp.data.BPTextContainer;
import bp.data.BPTextContainerBase;
import bp.format.BPFormat;
import bp.format.BPFormatHTML;
import bp.res.BPResource;
import bp.res.BPResourceFileSystem;
import bp.ui.scomp.BPHTMLCodePane;
import bp.ui.scomp.BPHTMLView;
import bp.ui.scomp.BPEditorPane;
import bp.ui.scomp.BPTextPane;
import bp.ui.util.UIUtil;

public class BPHTMLPanel extends BPTextPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5642804718744148187L;

	protected BPHTMLView m_preview;
	protected Consumer<BPEditorPane> m_changedhandler;
	protected AtomicBoolean m_changed = new AtomicBoolean(false);
	protected boolean m_canpreview = true;
	protected JPanel m_sp;
	protected JScrollPane m_pdest;

	public BPHTMLPanel()
	{
		init();
	}

	protected void init()
	{
		setLayout(new BorderLayout());
		m_sp = new JPanel();
		m_sp.setLayout(new GridLayout(1, 2, 0, 0));
		JScrollPane psrc = new JScrollPane();
		m_pdest = new JScrollPane();

		m_txt = createTextPane();
		m_changedhandler = this::onTextChanged;
		m_txt.setChangedHandler(m_changedhandler);
		m_preview = new BPHTMLView();

		psrc.setBorder(new EmptyBorder(0, 0, 0, 0));
		m_pdest.setBorder(new MatteBorder(0, 1, 0, 0, UIConfigs.COLOR_WEAKBORDER()));

		m_sp.add(psrc);
		m_sp.add(m_pdest);

		psrc.setViewportView(m_txt);
		m_pdest.setViewportView(m_preview);

		initActions();
		add(m_sp, BorderLayout.CENTER);

		m_txt.setOnPosChanged(this::onPosChanged);
		m_txt.resizeDoc();
	}

	protected void onTextChanged(BPEditorPane comp)
	{
		if (m_canpreview)
		{
			m_changed.set(true);
			UIUtil.laterUI(() ->
			{
				if (m_changed.compareAndSet(true, false))
				{
					m_preview.setHTML(comp.getText());
				}
			});
		}
	}

	public void toggleRightPanel()
	{
		boolean canpreview = !m_canpreview;
		m_canpreview = canpreview;
		if (canpreview)
		{
			m_sp.add(m_pdest);
			onTextChanged(m_txt);
		}
		else
		{
			m_sp.remove(m_pdest);
		}
		m_sp.validate();
	}

	protected BPTextPane createTextPane()
	{
		return new BPHTMLCodePane();
	}

	public final static class BPEditorFactoryHTML implements BPEditorFactory
	{
		public String[] getFormats()
		{
			return new String[] { BPFormatHTML.FORMAT_HTML };
		}

		public BPEditor<?> createEditor(BPFormat format, BPResource res, BPConfig options, Object... params)
		{
			return new BPHTMLPanel();
		}

		public void initEditor(BPEditor<?> editor, BPFormat format, BPResource res, BPConfig options)
		{
			if (res.isFileSystem() && ((BPResourceFileSystem) res).isFile())
			{
				BPTextContainer con = new BPTextContainerBase();
				con.bind(res);
				((BPHTMLPanel) editor).bind(con);
			}
		}

		public String getName()
		{
			return "HTML Editor";
		}
	}

	public void clearResource()
	{
		m_preview.clearResource();
		super.clearResource();
	}

	public void activeEditor()
	{
		m_txt.resizeDoc();
	}
}