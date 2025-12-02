# Sign the built EXE file with self-signed certificate
# Run this AFTER building your application

$certPath = "$PSScriptRoot\pqc-codesign.pfx"
$certPassword = "BiltyGenerator2025" # Must match the password from create-self-signed-cert.ps1

# Find signtool.exe
$signtoolPaths = @(
    "C:\Program Files (x86)\Windows Kits\10\bin\*\x64\signtool.exe",
    "C:\Program Files\Windows Kits\10\bin\*\x64\signtool.exe"
)

$signtool = $null
foreach ($path in $signtoolPaths) {
    $found = Get-ChildItem $path -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($found) {
        $signtool = $found.FullName
        break
    }
}

if (-not $signtool) {
    Write-Host "ERROR: signtool.exe not found!" -ForegroundColor Red
    Write-Host "Please install Windows SDK from:" -ForegroundColor Yellow
    Write-Host "https://developer.microsoft.com/windows/downloads/windows-sdk/" -ForegroundColor Cyan
    exit 1
}

Write-Host "Using signtool: $signtool" -ForegroundColor Green

# Find the built EXE
$exePaths = @(
    "$PSScriptRoot\composeApp\build\compose\binaries\main\app\Bilty Generator\*.exe",
    "$PSScriptRoot\composeApp\build\compose\binaries\main-release\app\Bilty Generator\*.exe",
    "$PSScriptRoot\composeApp\build\compose\binaries\main\exe\*.exe"
)

$exeFile = $null
foreach ($path in $exePaths) {
    $found = Get-ChildItem $path -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($found) {
        $exeFile = $found.FullName
        break
    }
}

if (-not $exeFile) {
    Write-Host "ERROR: No EXE file found!" -ForegroundColor Red
    Write-Host "Please run: gradlew packageExe" -ForegroundColor Yellow
    exit 1
}

Write-Host "`nSigning EXE: $exeFile" -ForegroundColor Green

# Sign the EXE
& $signtool sign /f $certPath /p $certPassword /fd SHA256 /v $exeFile

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n=== SUCCESS ===" -ForegroundColor Green
    Write-Host "EXE signed successfully!" -ForegroundColor Green
    Write-Host "`nVerifying signature..." -ForegroundColor Cyan

    # Verify the signature
    & $signtool verify /pa /v $exeFile

    Write-Host "`n=== IMPORTANT ===" -ForegroundColor Yellow
    Write-Host "This is a SELF-SIGNED certificate." -ForegroundColor Yellow
    Write-Host "Users will STILL see SmartScreen warning." -ForegroundColor Yellow
    Write-Host "But it will show 'Parallel Quintillion Coders' as publisher." -ForegroundColor Cyan
} else {
    Write-Host "`n=== FAILED ===" -ForegroundColor Red
    Write-Host "Failed to sign EXE. Check errors above." -ForegroundColor Red
}

