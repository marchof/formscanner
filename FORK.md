# Fork

The fork was created on 2024/07/26 using the following commands:

```bash
# Install dependencies
sudo add-apt-repository ppa:mercurial-ppa/releases
sudo apt update
sudo apt-get install -y mercurial
python -m pip install mercurial


# Create temporary working directory
rm -rf tmp || true
mkdir tmp
cd tmp

git clone https://github.com/frej/fast-export.git
hg clone http://hg.code.sf.net/p/formscanner/code formscanner-hg

git init formscanner-git
cd formscanner-git
../fast-export/hg-fast-export.sh -r ../formscanner-hg -A ../../authors
```

Where the `authors` file normalizes various usages in Mercurial of the original author's [superalberto76](https://github.com/superalberto76) name and email. The file is not shown here to protect his contact details.


The main branch of this Git repository is based on the [1.1.4](https://github.com/marchof/formscanner/releases/tag/1.1.4) tag.