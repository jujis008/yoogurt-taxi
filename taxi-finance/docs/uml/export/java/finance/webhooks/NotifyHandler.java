package finance.webhooks;

import java.util.Map;

public interface NotifyHandler {

	/**
	 * 处理回调事件
	 */
	public abstract Event handle(Map params);

}
