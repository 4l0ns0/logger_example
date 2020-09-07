# logger_example

# Conexion a base de datos
Los parametros de conexion se especifican con la clase settings, tanto en la clase Main como en el Test.
> new Settings("root", "sa", "mysql", "logger_server", "3306", "db_logger");

# Compilacion
Ejecutar el maven wrapper con la siguiente instruccion para que añada al jar resultante todas las librerias necesarias

> mvnw.cmd compile assembly:single

# Inicializaciones
Se incluye un directorio 'resources' con el script de creacion de tabla y el dockerfile para la creacion de la imagen

> 1.- Para crear la imagen colocar el jar dentro de un directorio bin en la misma ubicacion del docker file.

> 2.- Ejecutar: docker build -t logger .

# Ejecucion
Ejecutar el contenedor en la misma red del servidor de base de datos
> docker run --network mysql_network logger

> Al ejecutar el jar se llamara a la clase Main y se ejecutar algunos loggins
