package bp.ui.actions;

import java.util.Map;

import bp.ui.res.icon.BPIconResV;

public class BPActionHelperWebCFs extends BPActionHelperBase<BPActionConstWebCFs>
{
	public final static String ACTIONHELPER_PACK_WEBCFS = "webcfs";

	public String getPackName()
	{
		return ACTIONHELPER_PACK_WEBCFS;
	}

	public void initDefaults(Map<Integer, Object> actmap)
	{
		putAction(actmap, BPActionConstWebCFs.ACT_BTNGETHOST, "gethost", "Get Hostname", BPIconResV::DROPDOWN, null, null);
		putAction(actmap, BPActionConstWebCFs.ACT_BTNSENDPING, "ping", "Send Ping", BPIconResV::START, null, null);
		putAction(actmap, BPActionConstWebCFs.TNAME_ADDRINFO, "Address Info", null, null, null, null);
		putAction(actmap, BPActionConstWebCFs.TNAME_SEARCHENGINE, "Search Engine", null, null, null, null);
		putAction(actmap, BPActionConstWebCFs.TXT_WEBSEARCHENGINE, "Web Search Engine", null, null, null, null);
	}

	protected Class<BPActionConstWebCFs> getConstClass()
	{
		return BPActionConstWebCFs.class;
	}
}
