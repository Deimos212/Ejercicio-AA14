
public class Chocolate extends Golosina{
	String nombre;
	int porcentajeCacao;

	public Chocolate(String nombre, String sabor, int azucar, int porcentajeCacao) {
		super(sabor, azucar);
		this.porcentajeCacao = porcentajeCacao;
		this.nombre = nombre;
	}
	
}
