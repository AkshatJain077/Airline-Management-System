@echo off
if not exist out mkdir out
javac -d out src\airline\MainApp.java src\airline\db\DatabaseManager.java src\airline\model\AppUser.java src\airline\model\Flight.java src\airline\model\Booking.java src\airline\model\RouteStop.java src\airline\service\AuthService.java src\airline\service\AirlineService.java src\airline\ui\UIStyle.java src\airline\ui\LoginFrame.java src\airline\ui\AirlineManagementFrame.java
if errorlevel 1 goto end
java --enable-native-access=ALL-UNNAMED -cp "out;lib/*" airline.MainApp
:end
