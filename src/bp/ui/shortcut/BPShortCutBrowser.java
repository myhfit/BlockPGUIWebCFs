package bp.ui.shortcut;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import bp.browser.BPBrowserHelperManager;
import bp.config.BPSetting;
import bp.config.BPSettingBase;
import bp.config.BPSettingItem;
import bp.env.BPEnv;
import bp.env.BPEnvBrowser;
import bp.env.BPEnvManager;
import bp.util.ObjUtil;
import bp.util.TextUtil;

public class BPShortCutBrowser extends BPShortCutBase
{
	public final static String SCKEY_BROWSER = "Browser";

	protected final static String SC_KEY_BROWSER = "browser";
	protected final static String SC_KEY_URL = "url";
	protected final static String SC_KEY_APPMODE = "appmode";
	protected final static String SC_KEY_SIZE = "size";
	protected final static String SC_KEY_POS = "pos";

	public String getShortCutKey()
	{
		return SCKEY_BROWSER;
	}

	public boolean run()
	{
		String url = TextUtil.eds(getParam(SC_KEY_URL));
		String browser = TextUtil.eds(getParam(SC_KEY_BROWSER));
		BPBrowserHelperManager.open(browser, url, ObjUtil.makeMap("appmode", ObjUtil.toBool(getParam(SC_KEY_APPMODE), false), "size", TextUtil.eds(getParam(SC_KEY_SIZE)), "pos", TextUtil.eds(getParam(SC_KEY_POS))));
		return true;
	}

	protected String[] getParamKeys()
	{
		return new String[] { SC_KEY_URL, SC_KEY_BROWSER, SC_KEY_APPMODE, SC_KEY_SIZE, SC_KEY_POS };
	}

	public BPSetting getSetting()
	{
		BPSettingBase rc = (BPSettingBase) super.getSetting();
		List<String> browsers = new ArrayList<String>();
		{
			BPEnv env = BPEnvManager.getEnv(BPEnvBrowser.ENV_NAME_BROWSER);
			List<String> ks = env.listKeys();
			for (String k : ks)
			{
				if (!BPEnvBrowser.ENVKEY_BROWSER_DEFAULT.equals(k))
					browsers.add(k);
			}
		}

		rc.addItem(BPSettingItem.create(SC_KEY_URL, "URL", BPSettingItem.ITEM_TYPE_TEXT, null));
		rc.addItem(BPSettingItem.create(SC_KEY_BROWSER, "Browser", BPSettingItem.ITEM_TYPE_SELECT, browsers.toArray(new String[browsers.size()])));
		rc.addItem(BPSettingItem.create(SC_KEY_APPMODE, "AppMode", BPSettingItem.ITEM_TYPE_SELECT, new String[] { "true", "false" }));
		rc.addItem(BPSettingItem.create(SC_KEY_SIZE, "Size", BPSettingItem.ITEM_TYPE_TEXT, null));
		rc.addItem(BPSettingItem.create(SC_KEY_POS, "Position", BPSettingItem.ITEM_TYPE_TEXT, null));

		rc.setAll(m_params);
		return rc;
	}

	public void setSetting(BPSetting setting)
	{
		super.setSetting(setting);
		m_params = setParamsFromSetting(new LinkedHashMap<String, Object>(), setting, true, false, SC_KEY_URL, SC_KEY_BROWSER, SC_KEY_APPMODE, SC_KEY_SIZE, SC_KEY_POS);
	}
}
