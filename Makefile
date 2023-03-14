.PHONY = build
.PHONY += lint

MVN_ARGS ?= -ntp
MVN_CMD ?= mvn

#################################################
# Targets
#################################################
default: help

build: ## Create the JAR file
	$(MVN_CMD) $(MVN_ARGS) package

coverage: test ## Generate code coverage reports
	$(MVN_CMD) $(MVN_ARGS) jacoco:report

clean: ## Cleans up build artifacts
	$(MVN_CMD) $(MVN_ARGS) clean

lint: ## Check formatting
	$(MVN_CMD) $(MVN_ARGS) checkstyle:check

test: ## Run unittests
	$(MVN_CMD) $(MVN_ARGS) test

help: ## This message
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'
