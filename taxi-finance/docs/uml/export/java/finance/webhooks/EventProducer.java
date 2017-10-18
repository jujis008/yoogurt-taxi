package finance.webhooks;

public interface EventProducer {

	public abstract EventTask product(Event event);

	public abstract EventTask retry(Event event);

}
