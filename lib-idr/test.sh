#!/usr/bin/env bash

echo ""
echo "==========================================="
echo "========= Rebuilding before tests ========="
echo "==========================================="
echo ""

idris2 --clean test.ipkg
idris2 --build test.ipkg

echo ""
echo "==========================================="
echo "=========     Executing tests     ========="
echo "==========================================="
echo ""

.build/exec/test
