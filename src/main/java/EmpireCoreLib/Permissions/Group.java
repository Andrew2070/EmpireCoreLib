package EmpireCoreLib.Permissions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.ImmutableList;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import EmpireCoreLib.JSON.SerializerTemplate;
import EmpireCoreLib.Permissions.Meta.Container;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class Group {

	String 				Name 			= "";
	Integer				Rank;
	String 				Prefix 			= "";
	String 				Suffix			= "";
	List<User> 			Users 			= new ArrayList<User>();
	List<Group>			Parents			= null;
	
	public final 	PermissionsContainer 	permsContainer 		= new PermissionsContainer();
	public final    Meta.Container 			metaContainer	    = new Meta.Container();
	public final 	Container 				parents 			= new Container();


	
	public Group(String Name, Integer Rank, String Prefix, String Suffix) {
		setName(Name);
		setRank(Rank);
		setPrefix(Prefix);
		setSuffix(Suffix);
		
	}
	
	public Group() {
		this.Name = "default";
		Node 		EmpireCore 	 = new Node(null, 		"EmpireCore", "root permission node", 	   	DefaultPermissionLevel.ALL);
		Node	    command		 = new Node(EmpireCore, "cmd", 		  "command node", 			   	DefaultPermissionLevel.ALL);
		Node	    commandAll   = new Node(command, 	"cmd.*", 	  "All command node children", 	DefaultPermissionLevel.ALL);
		
		this.permsContainer.add(EmpireCore);			
		this.permsContainer.add(command);			
		this.permsContainer.add(commandAll);
	}
	
	public Group(String name) {
		this.Name = name;
	}
	
	public SimplePermissionLevels hasPermission(String permission) {
		SimplePermissionLevels permLevel = permsContainer.hasPermission(permission);

		if (permLevel == SimplePermissionLevels.DENIED || permLevel == SimplePermissionLevels.ALLOWED) {
			return permLevel;
		}

		// If nothing was found search the inherited permissions

		for (Group parent : Parents) {
			permLevel = parent.hasPermission(permission);
			if (permLevel == SimplePermissionLevels.DENIED || permLevel == SimplePermissionLevels.ALLOWED) {
				return permLevel;
			}
		}

		return permLevel;
	}
	
	//Set Methods:
	
	public void setName(String name) {
		this.Name = name;
	}
	
	public void setRank(int rank) {
		this.Rank = rank;
	}
	
	public void setPrefix(String prefix) {
		this.Prefix = prefix;
	}
	
	public void removePrefix() {
		this.Prefix = "";
	}
	
	public void setSuffix(String suffix) {
		this.Suffix = suffix;
	}
	
	public void removeSuffix() {
		this.Suffix = "";
	}
	
	public void setParent(Group parent) {
		for (Group group : Parents) {
			
			if (group != parent) {
				this.Parents.add(group);
			}
			
		}
	}
	
	public void removeParent(Group parent) {
		for (Group group : Parents) {
			if (group == parent) {
				this.Parents.remove(parent);
			}
		}
	}
	
	public void setNode(Node node) {
		if ( this.permsContainer.contains(node) == false) {
			this.permsContainer.add(node);
		}
	}
	
	public void removeNode(Node node) {
		if ( this.permsContainer.contains(node) == true) {
			this.permsContainer.remove(node);
		}
	}
	
	public void setUser(User user) {
		this.Users.add(user);
	}
	
	public void removeUser(User user) {
		if (this.Users.contains(user) == true) {
			this.Users.remove(user);
		}
	}
	
	//Get Methods:
	public Node getNodeFromString(String nodename) {
		for (Node node : permsContainer) {
			if (node.getName() == nodename) {
				return node;
			}
			
			if (node.getLocalizedName() == nodename) {
				return node;
			}
			
		}
		return null;
	}
		
	public String getName() {
		return this.Name;	
	}
	
	public int getRank() {
		return this.Rank;
	}
	
	public String getPrefix() {
		return this.Prefix;	
	}
	
	public String getSuffix() {
		return this.Suffix;	
	}
	
	public Node getNodes() {
		for (int i=0; i < this.permsContainer.size(); i++) {
			return this.permsContainer.get(i);
		}
	return null;		
	}

	public User getUsers() {
		for (int i=0; i < this.Users.size(); i++) {
			return this.Users.get(i);
		}
	return null;	
	}
	
	public static class Serializer extends SerializerTemplate<Group> {

		@Override
		public void register(GsonBuilder builder) {
			builder.registerTypeAdapter(Group.class, this);
			new Meta.Container.Serializer().register(builder);
		}

		@Override
		public Group deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();
			String name = jsonObject.get("name").getAsString();
			Group group = new Group(name);
			if (jsonObject.has("permissions")) {
				group.permsContainer.addAll(ImmutableList
						.copyOf(context.<Node[]>deserialize(jsonObject.get("permissions"), Node[].class)));	
				
			}
				  group.setRank(jsonObject.get("Rank").getAsInt());
				  group.setPrefix(jsonObject.get("Prefix").getAsString());
				  group.setSuffix(jsonObject.get("Suffix").getAsString());
				  
			if (jsonObject.has("meta")) {
				group.metaContainer
						.addAll(context.<Meta.Container>deserialize(jsonObject.get("meta"), Meta.Container.class));
			}
			return group;
		}

		@Override
		public JsonElement serialize(Group group, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject json = new JsonObject();
			json.addProperty("name", group.getName());
			json.add("Rank", context.serialize(group.getRank()));
			json.add("Prefix", context.serialize(group.getPrefix()));
			json.add("Suffix", context.serialize(group.getSuffix()));
		json.add("permissions", context.serialize(group.permsContainer));
		//	json.add("Properties:", context.serialize(group));
			json.add("meta", context.serialize(group.metaContainer));
			return json;
		}
	}
	
	public static class Container extends ArrayList<Group> {

		public void remove(String groupName) {
			for (Iterator<Group> it = iterator(); it.hasNext();) {
				Group group = it.next();
				if (group.getName().equals(groupName)) {
					it.remove();
					return;
				}
			}
		}

		public boolean contains(String groupName) {
			for (Group group : this) {
				if (group.getName().equals(groupName))
					return true;
			}
			return false;
		}
		public Group get(String groupName) {
			for (Group group : this) {
				if (group.getName().equals(groupName))
					return group;
			}
			return null;
		}
}
}