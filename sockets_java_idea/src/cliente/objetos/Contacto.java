package cliente.objetos;

public class Contacto {
    String ip;
    String nombre;

    public Contacto(){
    
    }
    public Contacto(String ip, String nombre) {
        this.ip = ip;
        this.nombre = nombre;
    }

    public String getIp() {
        return ip;
    }
    public String getNombre() {
        return nombre;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    @Override
    public String toString() {
        return "Contacto ip:" + ip + ", Nombre:" + nombre;
    } 

    
}
