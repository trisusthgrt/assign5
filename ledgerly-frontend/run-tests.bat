@echo off
echo Running Angular Tests with Coverage...
echo.

echo Installing dependencies if needed...
npm install

echo.
echo Running tests with coverage...
ng test --code-coverage --watch=false

echo.
echo Tests completed!
pause
