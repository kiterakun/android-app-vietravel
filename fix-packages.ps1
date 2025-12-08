# PowerShell Script to Fix Package Declarations

Write-Host "🔄 FIXING PACKAGE DECLARATIONS..." -ForegroundColor Cyan
Write-Host ""

# Define file patterns and their new packages
$packageFixes = @(
    @{
        Pattern = "E:\Mobile_vietravel\android-app-vietravel\app\src\main\java\com\group6\vietravel\features\user\ui\adapters\*.java"
        OldPackage = "package com.group6.vietravel.adapters;"
        NewPackage = "package com.group6.vietravel.features.user.ui.adapters;"
    },
    @{
        Pattern = "E:\Mobile_vietravel\android-app-vietravel\app\src\main\java\com\group6\vietravel\features\user\ui\main\*.java"
        OldPackage = "package com.group6.vietravel.ui.main;"
        NewPackage = "package com.group6.vietravel.features.user.ui.main;"
    },
    @{
        Pattern = "E:\Mobile_vietravel\android-app-vietravel\app\src\main\java\com\group6\vietravel\features\user\ui\main\account\*.java"
        OldPackage = "package com.group6.vietravel.ui.main.account;"
        NewPackage = "package com.group6.vietravel.features.user.ui.main.account;"
    },
    @{
        Pattern = "E:\Mobile_vietravel\android-app-vietravel\app\src\main\java\com\group6\vietravel\features\user\ui\main\chatbot\*.java"
        OldPackage = "package com.group6.vietravel.ui.main.chatbot;"
        NewPackage = "package com.group6.vietravel.features.user.ui.main.chatbot;"
    },
    @{
        Pattern = "E:\Mobile_vietravel\android-app-vietravel\app\src\main\java\com\group6\vietravel\features\user\ui\main\dialog\*.java"
        OldPackage = "package com.group6.vietravel.ui.main.dialog;"
        NewPackage = "package com.group6.vietravel.features.user.ui.main.dialog;"
    },
    @{
        Pattern = "E:\Mobile_vietravel\android-app-vietravel\app\src\main\java\com\group6\vietravel\features\user\ui\main\discovery\*.java"
        OldPackage = "package com.group6.vietravel.ui.main.discovery;"
        NewPackage = "package com.group6.vietravel.features.user.ui.main.discovery;"
    },
    @{
        Pattern = "E:\Mobile_vietravel\android-app-vietravel\app\src\main\java\com\group6\vietravel\features\user\ui\main\journey\*.java"
        OldPackage = "package com.group6.vietravel.ui.main.journey;"
        NewPackage = "package com.group6.vietravel.features.user.ui.main.journey;"
    },
    @{
        Pattern = "E:\Mobile_vietravel\android-app-vietravel\app\src\main\java\com\group6\vietravel\features\user\ui\main\journey\favoriteLocation\*.java"
        OldPackage = "package com.group6.vietravel.ui.main.journey.favoriteLocation;"
        NewPackage = "package com.group6.vietravel.features.user.ui.main.journey.favoriteLocation;"
    },
    @{
        Pattern = "E:\Mobile_vietravel\android-app-vietravel\app\src\main\java\com\group6\vietravel\features\user\ui\main\journey\historyJourney\*.java"
        OldPackage = "package com.group6.vietravel.ui.main.journey.historyJourney;"
        NewPackage = "package com.group6.vietravel.features.user.ui.main.journey.historyJourney;"
    },
    @{
        Pattern = "E:\Mobile_vietravel\android-app-vietravel\app\src\main\java\com\group6\vietravel\features\user\ui\main\journey\myEvaluation\*.java"
        OldPackage = "package com.group6.vietravel.ui.main.journey.myEvaluation;"
        NewPackage = "package com.group6.vietravel.features.user.ui.main.journey.myEvaluation;"
    },
    @{
        Pattern = "E:\Mobile_vietravel\android-app-vietravel\app\src\main\java\com\group6\vietravel\features\user\ui\main\ranking\*.java"
        OldPackage = "package com.group6.vietravel.ui.main.ranking;"
        NewPackage = "package com.group6.vietravel.features.user.ui.main.ranking;"
    },
    @{
        Pattern = "E:\Mobile_vietravel\android-app-vietravel\app\src\main\java\com\group6\vietravel\features\user\ui\detail\*.java"
        OldPackage = "package com.group6.vietravel.ui.detail;"
        NewPackage = "package com.group6.vietravel.features.user.ui.detail;"
    },
    @{
        Pattern = "E:\Mobile_vietravel\android-app-vietravel\app\src\main\java\com\group6\vietravel\features\user\ui\search\*.java"
        OldPackage = "package com.group6.vietravel.ui.search;"
        NewPackage = "package com.group6.vietravel.features.user.ui.search;"
    }
)

$modifiedFiles = 0

foreach ($fix in $packageFixes) {
    $files = Get-ChildItem -Path $fix.Pattern -ErrorAction SilentlyContinue
    
    foreach ($file in $files) {
        $content = Get-Content -Path $file.FullName -Raw
        
        if ($content -match [regex]::Escape($fix.OldPackage)) {
            $content = $content -replace [regex]::Escape($fix.OldPackage), $fix.NewPackage
            Set-Content -Path $file.FullName -Value $content -NoNewline
            $modifiedFiles++
            Write-Host "✅ Fixed package: $($file.Name)" -ForegroundColor Green
        }
    }
}

Write-Host ""
Write-Host "════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "✅ PACKAGE FIX COMPLETED!" -ForegroundColor Green
Write-Host "   Files modified: $modifiedFiles" -ForegroundColor Yellow
Write-Host "════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""
