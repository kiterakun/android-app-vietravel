# PowerShell Script to Fix Imports After User Module Restructure

Write-Host "🔄 FIXING IMPORTS FOR USER MODULE..." -ForegroundColor Cyan
Write-Host ""

$projectRoot = "E:\Mobile_vietravel\android-app-vietravel\app\src\main\java"

# Define replacements
$replacements = @{
    # User UI classes
    "import com.group6.vietravel.features.ui.adapters." = "import com.group6.vietravel.features.user.ui.adapters."
    "import com.group6.vietravel.features.ui.main." = "import com.group6.vietravel.features.user.ui.main."
    "import com.group6.vietravel.features.ui.detail." = "import com.group6.vietravel.features.user.ui.detail."
    "import com.group6.vietravel.features.ui.search." = "import com.group6.vietravel.features.user.ui.search."
}

# Get all Java files
$javaFiles = Get-ChildItem -Path $projectRoot -Filter "*.java" -Recurse

$totalFiles = $javaFiles.Count
$processedFiles = 0
$modifiedFiles = 0

Write-Host "📁 Found $totalFiles Java files to process" -ForegroundColor Yellow
Write-Host ""

foreach ($file in $javaFiles) {
    $processedFiles++
    $modified = $false
    
    # Read file content
    $content = Get-Content -Path $file.FullName -Raw
    $originalContent = $content
    
    # Apply replacements
    foreach ($old in $replacements.Keys) {
        $new = $replacements[$old]
        if ($content -match [regex]::Escape($old)) {
            $content = $content -replace [regex]::Escape($old), $new
            $modified = $true
        }
    }
    
    # Write back if modified
    if ($modified) {
        Set-Content -Path $file.FullName -Value $content -NoNewline
        $modifiedFiles++
        Write-Host "✅ Fixed: $($file.Name)" -ForegroundColor Green
    }
    
    # Progress
    if ($processedFiles % 10 -eq 0) {
        $percent = [math]::Round(($processedFiles / $totalFiles) * 100)
        Write-Host "Progress: $percent% ($processedFiles/$totalFiles)" -ForegroundColor Cyan
    }
}

Write-Host ""
Write-Host "════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "✅ IMPORT FIX COMPLETED!" -ForegroundColor Green
Write-Host "   Total files processed: $totalFiles" -ForegroundColor White
Write-Host "   Files modified: $modifiedFiles" -ForegroundColor Yellow
Write-Host "════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""
