#  Setting up an SSH Connection to GitHub
The following are the required steps to set up an SSH connection to GitHub either by using an existing SSH key or by generating a new one 

```Why is this needed?```

CardIO makes use of `dmz` as a submodule, a repository which requires an SSH connection to GitHub to access 


## Step 1 - Check for existing SSH Keys 

- Check for existing SSH keys using
        ```ls -al ~/.ssh```
- You’re looking for  ```id_rsa.pub``` If this exists go to step 3
- If this doesn’t exist OR  you receive an error that ~/.ssh doesn't exist then go to step 2 

## Step 2 - Generating an SSH key

- Paste the text below, substituting in your GitHub email address.
        ```ssh-keygen -t rsa -b 4096 -C "your_email@example.com"```
- When you're prompted to "Enter a file in which to save the key," press Enter. This accepts the default file location.
- At the prompt, type a secure passphrase

## Step 3 - Adding your SSH key to the SSH-Agent
- Start the ssh-agent in the background:
        ```eval "$(ssh-agent -s)"```
- Modify the SSH config file at:
        ```~/.ssh/config```
- And add the following
        ```Host *
            AddKeysToAgent yes
            UseKeychain yes
            IdentityFile ~/.ssh/id_rsa```
- Add your SSH private key to the ssh-agent and store your passphrase in the keychain. If you created your key with a different name, or if you are adding an existing key that has a different name, replace id_rsa in the command with the name of your private key file:
        ```ssh-add -K ~/.ssh/id_rsa```

## Step 4 - Adding your SSH key to GitHub

- Copy the SSH key to your clipboard:
        ```pbcopy < ~/.ssh/id_rsa.pub```
- Go to GitHub
- In the upper-right corner of any page, click your profile photo, then click Settings
- In the user settings sidebar, click SSH and GPG keys
- Click New SSH key or Add SSH key.
- In the "Title" field, add a descriptive label for the new key. For example, if you're using a personal Mac, you might call this key "Personal MacBook Air".
- Paste your key into the "Key" field.
- Click Add SSH key.

## Step 5 - Adding GitHub to SSH whitelist

- SSH to GitHub:
        ```ssh -T git@github.com```
- Verify that the fingerprint in the message you see matches one of the messages in step 2, then type yes
- Verify that the resulting message contains your username.

——————————————————————————————————

If all of the above went well then you're good to go! You can now finish the Carthage step of the installation section of the readme that you came from by running the following command:
        ```carthage update --platform iOS```  
