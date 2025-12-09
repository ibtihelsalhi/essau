# Docker Images Access & Management Script for Prestify (PowerShell)
# Usage: .\docker-images-util.ps1 -Command "list"

param(
    [Parameter(Position=0)]
    [ValidateSet("list", "inspect", "history", "logs", "logs-follow", "shell", "health", "stats", "clean", "push", "help")]
    [string]$Command = "help"
)

$PRESTIFY_IMAGE = "prestify-prestify_app:latest"
$PRESTIFY_CONTAINER = "prestify_app"
$REGISTRY = "registry.gitlab.com/yasmine.bouzid2001/project-devops"

function Show-Help {
    Write-Host ""
    Write-Host "=== Docker Images Management ===" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Usage: .\docker-images-util.ps1 -Command 'command'" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Commands:" -ForegroundColor Green
    Write-Host "  list              List all Prestify images"
    Write-Host "  inspect           Inspect current Prestify image details"
    Write-Host "  history           Show image build layers/history"
    Write-Host "  logs              Show container logs (last 50 lines)"
    Write-Host "  logs-follow       Follow container logs in real-time"
    Write-Host "  shell             Open shell in running container"
    Write-Host "  health            Check container health status"
    Write-Host "  stats             Show container resource usage"
    Write-Host "  clean             Remove old images"
    Write-Host "  push              Push image to GitLab Registry"
    Write-Host "  help              Show this help message"
    Write-Host ""
    Write-Host "Examples:" -ForegroundColor Cyan
    Write-Host "  .\docker-images-util.ps1 -Command list"
    Write-Host "  .\docker-images-util.ps1 -Command logs"
    Write-Host "  .\docker-images-util.ps1 -Command health"
    Write-Host ""
}

function List-Images {
    Write-Host "📦 PRESTIFY IMAGES:" -ForegroundColor Cyan
    docker images | Where-Object { $_ -match "prestify" }
    Write-Host ""
    Write-Host "📊 IMAGE SIZE DETAILS:" -ForegroundColor Yellow
    docker images --filter "reference=prestify*" --format "table {{.Repository}}`t{{.Tag}}`t{{.Size}}`t{{.CreatedAt}}"
}

function Inspect-Image {
    Write-Host "🔍 INSPECTING: $PRESTIFY_IMAGE" -ForegroundColor Cyan
    docker inspect $PRESTIFY_IMAGE | ConvertFrom-Json | ForEach-Object {
        Write-Host ""
        Write-Host "Image ID: $($_.Id)" -ForegroundColor Yellow
        Write-Host "Tags: $($_.RepoTags -join ', ')" -ForegroundColor Yellow
        Write-Host "Size: $('{0:N2}' -f ($_.Size / 1MB)) MB" -ForegroundColor Yellow
        Write-Host "Created: $($_.Created)" -ForegroundColor Yellow
        Write-Host "OS: $($_.Os) / Architecture: $($_.Architecture)" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Configuration:" -ForegroundColor Green
        Write-Host "  User: $($_.Config.User)"
        Write-Host "  Exposed Ports: $(($_.Config.ExposedPorts | Get-Member -MemberType NoteProperty).Name -join ', ')"
        Write-Host "  Entrypoint: $($_.Config.Entrypoint -join ' ')"
        Write-Host ""
    }
}

function Show-History {
    Write-Host "📜 BUILD HISTORY: $PRESTIFY_IMAGE" -ForegroundColor Cyan
    docker history $PRESTIFY_IMAGE --human --no-trunc
}

function Show-Logs {
    Write-Host "📝 CONTAINER LOGS (last 50 lines):" -ForegroundColor Cyan
    docker logs $PRESTIFY_CONTAINER --tail 50
}

function Follow-Logs {
    Write-Host "📝 FOLLOWING LOGS (Ctrl+C to stop):" -ForegroundColor Cyan
    docker logs $PRESTIFY_CONTAINER -f --tail 100
}

function Open-Shell {
    Write-Host "🔓 OPENING SHELL IN CONTAINER..." -ForegroundColor Green
    docker exec -it $PRESTIFY_CONTAINER /bin/sh
    Write-Host "✅ Shell closed" -ForegroundColor Green
}

function Check-Health {
    Write-Host "🏥 CONTAINER HEALTH CHECK:" -ForegroundColor Cyan
    
    $status = docker inspect $PRESTIFY_CONTAINER --format='{{.State.Health.Status}}'
    Write-Host "Health Status: $status" -ForegroundColor Yellow
    Write-Host ""
    
    Write-Host "Testing Java Version:" -ForegroundColor Green
    docker exec $PRESTIFY_CONTAINER java -version
    Write-Host ""
    
    Write-Host "Testing Application Actuator:" -ForegroundColor Green
    $result = docker exec $PRESTIFY_CONTAINER curl -s http://localhost:8080/actuator/health 2>&1
    if ($result) {
        Write-Host $result
    } else {
        Write-Host "⚠️  Health endpoint timeout (expected due to IPv4/IPv6 binding)" -ForegroundColor Yellow
    }
}

function Show-Stats {
    Write-Host "📊 CONTAINER RESOURCE USAGE:" -ForegroundColor Cyan
    docker stats $PRESTIFY_CONTAINER --no-stream
}

function Clean-Images {
    Write-Host "🧹 CLEANING OLD IMAGES..." -ForegroundColor Yellow
    
    $oldImage = "project_root-prestify-app:latest"
    $imageExists = docker images | Where-Object { $_ -match "project_root-prestify-app" }
    
    if ($imageExists) {
        Write-Host "Removing: $oldImage"
        docker rmi $oldImage -f
        Write-Host "✅ Removed" -ForegroundColor Green
    } else {
        Write-Host "No old images to remove" -ForegroundColor Yellow
    }
    
    Write-Host ""
    Write-Host "Pruning dangling images..." -ForegroundColor Yellow
    docker image prune -f --filter "dangling=true"
    Write-Host "✅ Cleanup complete" -ForegroundColor Green
}

function Push-ToRegistry {
    Write-Host "📤 PUSHING TO REGISTRY:" -ForegroundColor Cyan
    
    Write-Host "Note: This requires GitLab authentication" -ForegroundColor Yellow
    Write-Host "You can use .gitlab-ci.yml for automated registry push" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Manual push steps:" -ForegroundColor Green
    Write-Host "1. Login: docker login registry.gitlab.com" -ForegroundColor White
    Write-Host "2. Tag: docker tag $PRESTIFY_IMAGE ${REGISTRY}:latest" -ForegroundColor White
    Write-Host "3. Push: docker push ${REGISTRY}:latest" -ForegroundColor White
    Write-Host ""
    Write-Host "For automated pushes, use .gitlab-ci.yml pipeline" -ForegroundColor Cyan
}

# Main Switch
switch ($Command) {
    "list" { List-Images }
    "inspect" { Inspect-Image }
    "history" { Show-History }
    "logs" { Show-Logs }
    "logs-follow" { Follow-Logs }
    "shell" { Open-Shell }
    "health" { Check-Health }
    "stats" { Show-Stats }
    "clean" { Clean-Images }
    "push" { Push-ToRegistry }
    default { Show-Help }
}
