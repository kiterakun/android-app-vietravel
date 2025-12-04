# PowerShell Script to Fix Imports After Restructure

Write-Host "🔄 FIXING IMPORTS AFTER PROJECT RESTRUCTURE..." -ForegroundColor Cyan
Write-Host ""

$projectRoot = "E:\Mobile_vietravel\android-app-vietravel\app\src\main\java"

# Define replacements
$replacements = @{
    # Models
    "import com.group6.vietravel.data.models.User;" = "import com.group6.vietravel.data.models.user.User;"
    "import com.group6.vietravel.data.models.Favorite;" = "import com.group6.vietravel.data.models.user.Favorite;"
    "import com.group6.vietravel.data.models.VisitedPlace;" = "import com.group6.vietravel.data.models.user.VisitedPlace;"
    "import com.group6.vietravel.data.models.Place;" = "import com.group6.vietravel.data.models.place.Place;"
    "import com.group6.vietravel.data.models.Category;" = "import com.group6.vietravel.data.models.place.Category;"
    "import com.group6.vietravel.data.models.Province;" = "import com.group6.vietravel.data.models.place.Province;"
    "import com.group6.vietravel.data.models.District;" = "import com.group6.vietravel.data.models.place.District;"
    "import com.group6.vietravel.data.models.Review;" = "import com.group6.vietravel.data.models.review.Review;"
    
    # Repositories
    "import com.group6.vietravel.data.repositories.AuthRepository;" = "import com.group6.vietravel.data.repositories.auth.AuthRepository;"
    "import com.group6.vietravel.data.repositories.PlaceRepository;" = "import com.group6.vietravel.data.repositories.place.PlaceRepository;"
    "import com.group6.vietravel.data.repositories.ReviewRepository;" = "import com.group6.vietravel.data.repositories.review.ReviewRepository;"
    
    # UI - General pattern
    "import com.group6.vietravel.ui." = "import com.group6.vietravel.features.ui."
    
    # Adapters
    "import com.group6.vietravel.adapters." = "import com.group6.vietravel.features.ui.adapters."
    
    # Utils
    "import com.group6.vietravel.utils." = "import com.group6.vietravel.core.utils."
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
Write-Host "🔨 Next steps:" -ForegroundColor Yellow
Write-Host "   1. Open Android Studio" -ForegroundColor White
Write-Host "   2. File > Invalidate Caches / Restart" -ForegroundColor White
Write-Host "   3. Build > Clean Project" -ForegroundColor White
Write-Host "   4. Build > Rebuild Project" -ForegroundColor White
Write-Host ""
