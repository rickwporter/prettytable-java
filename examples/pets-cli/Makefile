.PHONY = build
.PHONY += clean
.PHONY += example
.PHONY += depend-check
.PHONY += depend-tree
.PHONY += lint
.PHONY += test

MVN_ARGS ?= -ntp
MVN_CMD ?= mvn

#################################################
# Targets
#################################################
default: help

all: lint depend-check test build ## Perform lint, dependency checking, coverage, and build

help: ## This message
	@echo "===================="
	@echo " Available Commands"
	@echo "===================="
	@awk 'BEGIN {FS = ":.*##"; printf "\nUsage:\n  make \033[36m\033[0m\n"} /^[$$()% a-zA-Z_-]+:.*?##/ { printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2 } /^##@/ { printf "\n\033[1m%s\033[0m\n", substr($$0, 5) } ' $(MAKEFILE_LIST)

###########
##@ Build
build: ## Create the JAR file
	$(MVN_CMD) $(MVN_ARGS)  -Dmaven.test.skip=true package

clean: ## Cleans up build artifacts
	$(MVN_CMD) $(MVN_ARGS) clean

###########
##@ Dependencies
depend-check:  ## Checks to make sure all dependencies are used (or declared unused)
	$(MVN_CMD) $(MVN_ARGS) dependency:analyze-only

depend-tree: ## Generates dependency reports
	$(MVN_CMD) $(MVN_ARGS) dependency:tree

###########
##@ Formatting
lint: ## Check formatting
	$(MVN_CMD) $(MVN_ARGS) checkstyle:check

###########
##@ Test
test: ## Run unittests
	$(MVN_CMD) $(MVN_ARGS) test

###########
##@ Example
example: ## Build and test the pets CLI example
	$(MVN_CMD) $(MVN_ARGS) -DskipTests package install:install
	$(MVN_CMD) $(MVN_ARGS) -f examples/pets-cli verify
