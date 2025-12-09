#!/bin/bash
# Docker Images Access & Management Script for Prestify
# Usage: ./docker-images-util.sh [command]

PRESTIFY_IMAGE="prestify-prestify_app:latest"
PRESTIFY_CONTAINER="prestify_app"
REGISTRY="registry.gitlab.com/yasmine.bouzid2001/project-devops"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Functions
show_help() {
    echo -e "${BLUE}=== Docker Images Management ===${NC}"
    echo ""
    echo "Usage: ./docker-images-util.sh [command]"
    echo ""
    echo "Commands:"
    echo "  list              List all Prestify images"
    echo "  inspect           Inspect current Prestify image details"
    echo "  history           Show image build layers/history"
    echo "  logs              Show container logs (last 50 lines)"
    echo "  logs-follow       Follow container logs in real-time"
    echo "  shell             Open shell in running container"
    echo "  health            Check container health status"
    echo "  stats             Show container resource usage"
    echo "  clean             Remove old images"
    echo "  push              Push image to GitLab Registry (requires auth)"
    echo "  help              Show this help message"
    echo ""
}

list_images() {
    echo -e "${BLUE}📦 PRESTIFY IMAGES:${NC}"
    docker images | grep prestify
    echo ""
    echo -e "${BLUE}📊 IMAGE DETAILS:${NC}"
    docker images --filter "reference=prestify*" --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}\t{{.CreatedAt}}"
}

inspect_image() {
    echo -e "${BLUE}🔍 INSPECTING: ${PRESTIFY_IMAGE}${NC}"
    docker inspect "${PRESTIFY_IMAGE}" | jq '{
        Id: .Id,
        RepoTags: .RepoTags,
        Size: .Size,
        Created: .Created,
        Architecture: .Architecture,
        Os: .Os,
        Config: {
            User: .Config.User,
            ExposedPorts: .Config.ExposedPorts,
            Env: .Config.Env[0:5],
            Entrypoint: .Config.Entrypoint,
            Cmd: .Config.Cmd
        }
    }'
}

show_history() {
    echo -e "${BLUE}📜 BUILD HISTORY: ${PRESTIFY_IMAGE}${NC}"
    docker history "${PRESTIFY_IMAGE}" --human --no-trunc
}

show_logs() {
    echo -e "${BLUE}📝 CONTAINER LOGS (last 50 lines):${NC}"
    docker logs "${PRESTIFY_CONTAINER}" --tail 50
}

follow_logs() {
    echo -e "${BLUE}📝 FOLLOWING LOGS (Ctrl+C to stop):${NC}"
    docker logs "${PRESTIFY_CONTAINER}" -f --tail 100
}

open_shell() {
    echo -e "${BLUE}🔓 OPENING SHELL IN CONTAINER...${NC}"
    if docker exec -it "${PRESTIFY_CONTAINER}" /bin/sh; then
        echo -e "${GREEN}✅ Shell closed${NC}"
    else
        echo -e "${RED}❌ Failed to open shell${NC}"
        exit 1
    fi
}

check_health() {
    echo -e "${BLUE}🏥 CONTAINER HEALTH CHECK:${NC}"
    STATUS=$(docker inspect "${PRESTIFY_CONTAINER}" --format='{{.State.Health.Status}}')
    echo "Health Status: $STATUS"
    echo ""
    
    echo -e "${BLUE}Testing Java Version:${NC}"
    docker exec "${PRESTIFY_CONTAINER}" java -version
    echo ""
    
    echo -e "${BLUE}Testing Application Actuator:${NC}"
    docker exec "${PRESTIFY_CONTAINER}" curl -s http://localhost:8080/actuator/health || echo "Note: Health endpoint timeout (expected due to IPv4/IPv6 binding)"
}

show_stats() {
    echo -e "${BLUE}📊 CONTAINER RESOURCE USAGE:${NC}"
    docker stats "${PRESTIFY_CONTAINER}" --no-stream
}

clean_images() {
    echo -e "${YELLOW}🧹 CLEANING OLD IMAGES...${NC}"
    
    OLD_IMAGE="project_root-prestify-app:latest"
    if docker images | grep -q "project_root-prestify-app"; then
        echo "Removing: $OLD_IMAGE"
        docker rmi "$OLD_IMAGE" -f
        echo -e "${GREEN}✅ Removed${NC}"
    else
        echo "No old images to remove"
    fi
    
    echo ""
    echo -e "${YELLOW}Pruning dangling images...${NC}"
    docker image prune -f --filter "dangling=true"
}

push_to_registry() {
    echo -e "${BLUE}📤 PUSHING TO REGISTRY:${NC}"
    
    if ! docker login registry.gitlab.com -u $GITLAB_USER -p $GITLAB_TOKEN > /dev/null 2>&1; then
        echo -e "${RED}❌ Authentication failed${NC}"
        echo "Please set GITLAB_USER and GITLAB_TOKEN environment variables"
        exit 1
    fi
    
    echo "Tagging image..."
    docker tag "${PRESTIFY_IMAGE}" "${REGISTRY}:latest"
    
    echo "Pushing to ${REGISTRY}:latest ..."
    docker push "${REGISTRY}:latest"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Push successful!${NC}"
    else
        echo -e "${RED}❌ Push failed${NC}"
        exit 1
    fi
}

# Main
case "${1:-help}" in
    list)
        list_images
        ;;
    inspect)
        inspect_image
        ;;
    history)
        show_history
        ;;
    logs)
        show_logs
        ;;
    logs-follow)
        follow_logs
        ;;
    shell)
        open_shell
        ;;
    health)
        check_health
        ;;
    stats)
        show_stats
        ;;
    clean)
        clean_images
        ;;
    push)
        push_to_registry
        ;;
    *)
        show_help
        ;;
esac
