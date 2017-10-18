package finance.webhooks;

import java.util.Map;

public interface WebhookService {

	public abstract Event notifyHandle(Map params);

}
