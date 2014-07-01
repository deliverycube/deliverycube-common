package deliverycube.common.atg.componentresolver;

public interface ComponentResolver {

	public abstract Object resolveComponent() throws UnknownComponentException;

}